package com.example.diseasesence

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.diseasesence.databinding.ActivityScanBinding
import com.example.diseasesence.ml.ModelUnquant
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Date
import java.util.UUID

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var auth: FirebaseAuth
    var TAG = "TAGY"
    var imageuri:Uri?= null
    var imageSize=224
    override fun onCreate(savedInstanceState: Bundle?){
    super.onCreate(savedInstanceState)

        binding.historybtn.setOnClickListener {

        }


    binding=DataBindingUtil.setContentView(this,R.layout.activity_scan)
    Toast.makeText(this,"${Users.instence?.userId}",Toast.LENGTH_SHORT).show()
        auth=Firebase.auth

        binding.imageView.setOnClickListener {
        var i=Intent(Intent.ACTION_PICK)
            i.type="image/*"
            startActivityForResult(i,100)
        }


        binding.takePick.setOnClickListener {

            if (checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent,3)

            }else{
                requestPermissions(arrayOf(Manifest.permission.CAMERA),300)
            }
        }
}



    //classifyimage

    private fun classifyImage(image: Bitmap) {
        try {
// Load your TensorFlow Lite model
            val model = ModelUnquant.newInstance(application)

// Prepare input image
            /*val inputFeature0 = TensorImage(DataType.FLOAT32)
            val bitmap = Bitmap.createScaledBitmap(image, 224, 224, true)
            inputFeature0.load(bitmap)*/


            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat(((`val` shr 16) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat(((`val` shr 8) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

// Run inference
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

// Retrieve class labels

            val classes = arrayOf("Healthy Apple ", "Apple Black Rot","Healthy Corn ","Corn leaf rust","Healthy Grape ","Grapevine Black Rot")
// Find the class with the highest confidence
            var maxPos = 0
            var maxConfidence = 0f
            for (i in classes.indices) {
                val confidence = outputFeature0.getFloatValue(i)
                if (confidence > maxConfidence) {
                    maxConfidence = confidence
                    maxPos = i
                }
            }

// Display result
            binding.itemname.text = classes[maxPos]

// Display confidence values for each class
            val description= arrayOf(
                "Maintaining healthy apple leaves is crucial for the overall health and productivity of apple trees. Here are some key factors to consider in promoting healthy apple leaves",
                "Black rot is a common fungal disease affecting apple trees, caused by the fungus Botryosphaeria obtusa. It primarily affects the leaves, fruit, and branches of apple trees, leading to significant yield losses if left unmanaged.",
                "Maintaining healthy corn leaves is essential for maximizing yield and overall plant health. Here are some key practices to promote healthy corn leaves.",
                "Corn leaf rust, caused by the fungus Puccinia sorghi, is a common foliar disease that affects corn plants. Here's some information about corn leaf common rust disease.",
                "Maintaining healthy grape leaves is crucial for ensuring vigorous vine growth, optimal photosynthesis, and high-quality grape production.",
                "Grapevine black rot, caused by the fungus Guignardia bidwellii, is a common and destructive disease affecting grapevines."
            )
            /*var s = ""
            for (i in classes.indices) {
                s += String.format("%s: %.1f%%\n", classes[i], outputFeature0.getFloatValue(i) * 100)
            }*/
            binding.desc.text=description[maxPos]

// Close the model to release resources
            model.close()

        } catch (e: IOException) {
            // TODO Handle the exception
            Toast.makeText(this,"${e.toString()}",Toast.LENGTH_SHORT).show()
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK){
            if (requestCode==3) {
                var image = data?.extras?.get("data") as Bitmap
                val dimension = minOf(image.width, image.height)
                val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                binding.imageView.setImageBitmap(image)
                val scaledImage = Bitmap.createScaledBitmap(thumbnail, 224, 224, false)
                classifyImage(scaledImage)
/*
                //Firebse FireStore
                val db=Firebase.firestore
                val collection=db.collection("Users")
                val storage=Firebase.storage
                val storageRef = storage.reference
                var uuid=UUID.randomUUID()
                val imagesRef = storageRef.child("${Users.instence?.userId}/${uuid}.jpg")
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                imagesRef.putBytes(data).addOnSuccessListener {
                        task ->
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        Toast.makeText(this, url.toString(), Toast.LENGTH_SHORT).show()
                        val time=Timestamp(Date())
                        val user=ItemModel(Users.instence?.userId.toString(),uuid.toString(),time,url.toString())
                        collection.document("$uuid").set(user)
                    }
                }.addOnCanceledListener {
                    Toast.makeText(this, "on cancelled", Toast.LENGTH_SHORT).show()
                }*/

            }else{
                imageuri=data?.data
                var image:Bitmap?=null
                try {
                    image=MediaStore.Images.Media.getBitmap(contentResolver,imageuri)
                }catch(e:IOException){

                }
                binding.imageView.setImageBitmap(image)

                val dimension = image?.let { minOf(it.width, image.height) }
                val thumbnail =
                    dimension?.let { ThumbnailUtils.extractThumbnail(image, it, dimension) }
                binding.imageView.setImageBitmap(image)
                val scaledImage = thumbnail?.let { Bitmap.createScaledBitmap(it, 224, 224, false) }
                if (scaledImage != null) {
                    classifyImage(scaledImage)
                }

                /*//Firebase Firebase
                val db=Firebase.firestore
                val collection=db.collection("Users")
                var uuid=UUID.randomUUID()
                var storage=Firebase.storage
                var storageRef=storage.reference
                imageuri.let {
                    if (it != null) {
                        Toast.makeText(this, "upload start", Toast.LENGTH_SHORT).show()
                        storageRef.child("${Users.instence?.userId}/${uuid}.jpg").putFile(it).addOnSuccessListener { task ->
                            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                                Toast.makeText(this, url.toString(), Toast.LENGTH_SHORT).show()
                                val time=Timestamp(Date())
                                val user=ItemModel(Users.instence?.userId.toString(),uuid.toString(),time,url.toString())
                                collection.document("$uuid").set(user)
                            }
                        }.addOnCanceledListener {
                            Toast.makeText(this, "on cancelled", Toast.LENGTH_SHORT).show()
                        }

                    }
                }*/

            }

        }
    }

}
