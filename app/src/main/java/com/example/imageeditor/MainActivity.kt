package com.example.imageeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.imageeditor.databinding.ActivityMainBinding
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.imageButton.setOnClickListener {
            imageChooser();
        }
        binding.editImage.setOnClickListener {
            binding.cropImage.visibility = View.VISIBLE
        }
        binding.cropImage.setOnClickListener {
            cropImage()
        }
        binding.exif.setOnClickListener {
            getExifData()
        }

    }

    private fun cropImage() {
        val intent:Intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(selectedImageUri,"image/*")
        intent.putExtra("crop",true)
        intent.putExtra("aspectX",1)
        intent.putExtra("aspectY",1)
        intent.putExtra("outputX",128)
        intent.putExtra("outputY",128)
        intent.putExtra("return-data",true)
     startActivityForResult(intent,123)
    }

    private fun imageChooser() {

        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "choose picture"), 1234)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri.let {
                binding.PreviewImage.setImageURI(selectedImageUri)
                binding.PreviewImage.drawable.let {
                    binding.editImage.visibility = View.VISIBLE
                    binding.exif.visibility = View.VISIBLE
                }
            }
        }
       if (requestCode == 123) {
           val extras: Bundle? = data?.extras
            val pic: Bitmap? = extras?.getParcelable("data")
            binding.PreviewImage.setImageBitmap(pic)
            binding.editImage.visibility = View.GONE
            binding.exif.visibility = View.GONE
            binding.cropImage.visibility = View.GONE
        }
    }


    fun getExifData() {
        var gfgUri: Uri? = selectedImageUri // the file uri

        val gfgIn: InputStream
        try {
            gfgIn = contentResolver.openInputStream(gfgUri!!)!!
            val exifInterface = ExifInterface(gfgIn)


            val attributes = arrayOf(
                ExifInterface.TAG_APERTURE_VALUE,
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_DATETIME_DIGITIZED,
                ExifInterface.TAG_DATETIME_ORIGINAL,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_FLASH,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_PROCESSING_METHOD,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_IMAGE_LENGTH,
                ExifInterface.TAG_IMAGE_WIDTH,
                ExifInterface.TAG_ISO_SPEED_RATINGS,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.TAG_SUBSEC_TIME,
                ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
                ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
                ExifInterface.TAG_WHITE_BALANCE
            )
            var s: String = ""
            for (i in attributes.indices) {
                val value = exifInterface.getAttribute(attributes[i])
                if (value != null) {
                    s = s + value + "\n"

                }
            }
            binding.textview.text = s

        } catch (e: IOException) {
        }
    }


}
