package com.jhoogstraat.fast_barcode_scanner.scanner

import android.graphics.ImageFormat
import android.media.Image
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.lang.Exception
import java.nio.ByteBuffer
import kotlin.experimental.inv

class MLKitBarcodeScanner(
    options: BarcodeScannerOptions,
    private val successListener: OnSuccessListener<List<Barcode>>,
    private val failureListener: OnFailureListener
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(options)

    // Android camera preview format
    private val imageFormatForManualInversion = ImageFormat.YUV_420_888

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val inputImage = preprocessImage(imageProxy)
        scanner.process(inputImage)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
                .addOnCompleteListener { imageProxy.close() }
    }

    @ExperimentalGetImage
    private fun preprocessImage(imageProxy: ImageProxy): InputImage {
        val originalImage = imageProxy.image!!
        if (originalImage.format != imageFormatForManualInversion) {
            return InputImage.fromMediaImage(originalImage, imageProxy.imageInfo.rotationDegrees)
        }
        return invertAndMapImage(originalImage, imageProxy.imageInfo.rotationDegrees)
    }

    private fun invertAndMapImage(originalImage: Image, rotationDegrees: Int): InputImage {
        originalImage.planes
                .filter { it.buffer != null }
                .forEach { invertPlaneBuffer(it.buffer) }
        return InputImage.fromMediaImage(originalImage, rotationDegrees)
    }

    private fun invertPlaneBuffer(buffer: ByteBuffer) {
        if (buffer.limit() == 0) {
            return
        }
        val cachedState = ByteArray(buffer.limit())
        buffer.rewind()
        buffer.get(cachedState)
        cachedState.forEachIndexed { index, byte ->
            cachedState[index] = byte.inv()
        }
        buffer.rewind()
        buffer.put(cachedState)
    }
}
