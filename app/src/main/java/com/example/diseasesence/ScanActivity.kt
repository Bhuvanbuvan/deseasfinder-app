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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.diseasesence.databinding.ActivityScanBinding
import com.example.diseasesence.ml.ModelUnquant
import com.example.diseasesence.ml.ModelUnquantt
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import org.tensorflow.lite.DataType
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


    override fun onCreate(savedInstanceState: Bundle?){
    super.onCreate(savedInstanceState)

    binding=DataBindingUtil.setContentView(this,R.layout.activity_scan)
    Toast.makeText(this,"${Users.instence?.userId}",Toast.LENGTH_SHORT).show()
        auth=Firebase.auth

        binding.imageView.setOnClickListener {
        var i=Intent(Intent.ACTION_PICK)
            i.type="image/*"
            startActivityForResult(i,100)
        }

        binding.launchgal.setOnClickListener {
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

            //val model =ModelUnquantt.newInstance(applicationContext)
            val model = com.example.diseasesence.ml.Model.newInstance(applicationContext)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * 32 * 32 * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(32 * 32)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until 32) {
                for (j in 0 until 32) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat(((`val` shr 16) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat(((`val` shr 8) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.getOutputFeature0AsTensorBuffer()

            val confidences = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            //val classes = arrayOf("1", "2","3","4","5","6","7","8","9","10")
            val classes = arrayOf("1", "2","3")

            binding.result.text = classes[maxPos]

            var s = ""
            for (i in classes.indices) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100)
            }

            binding.confidence.text = s

            // Releases model resources if no longer used.
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
                val dimension = minOf(image!!.width, image!!.height)
                val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                binding.imageView.setImageBitmap(thumbnail)
                val scaledImage = Bitmap.createScaledBitmap(thumbnail, 32, 32, false)
                classifyImage(scaledImage)

                //Firebse FireStore
               /* val db=Firebase.firestore
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
                val dimension = minOf(image!!.width, image!!.height)
                val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                val scaledImage = Bitmap.createScaledBitmap(thumbnail, 32, 32, false)
                classifyImage(scaledImage)

               /* //Firebase Firebase
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
