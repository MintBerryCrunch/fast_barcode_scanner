package com.jhoogstraat.fast_barcode_scanner

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MLKitBarcodeDetector(
    options: BarcodeScannerOptions,
    imageInversion: ImageInversion,
    private val successListener: OnSuccessListener<Text>,
    private val failureListener: OnFailureListener
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(options)
    private val invertor = ImageInvertor(imageInversion)
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

//    private var rotation = 0

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val inputImage = preprocessImage(imageProxy)
//        scanner.process(inputImage)
//                .addOnSuccessListener(successListener)
//                .addOnFailureListener(failureListener)
//                .addOnCompleteListener { imageProxy.close() }
//        Log.e("fast_barcode_scanner", "================= (settings) isThickClient: " + TextRecognizerOptions.DEFAULT_OPTIONS.isThickClient)
        textRecognizer.process(inputImage)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
            .addOnCompleteListener{ imageProxy.close() }
    }

    @ExperimentalGetImage
    private fun preprocessImage(imageProxy: ImageProxy): InputImage {
        val originalImage = imageProxy.image!!
        invertor.invertImageIfNeeded(originalImage)
//        rotation = (rotation + 90) % 360
//        Log.e("fast_barcode_scanner", "================= (rotation): " + rotation)
        return InputImage.fromMediaImage(originalImage, 90)
    }
}
