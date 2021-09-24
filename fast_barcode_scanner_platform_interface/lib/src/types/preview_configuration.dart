/// Supported resolutions. Not all devices support all resolutions!
enum Resolution { sd480, hd720, hd1080, hd4k }

/// Supported Framerates. Not all devices support all framerates!
enum Framerate { fps30, fps60, fps120, fps240 }

enum DetectionMode {
  /// Pauses the detection of further barcodes when a barcode is detected.
  /// The camera feed continues.
  pauseDetection,

  /// Pauses the camera feed on detection.
  /// This will inevitably stop the detection of barcodes.
  pauseVideo,

  /// Does nothing on detection. May need to throttle detections using continuous.
  continuous
}

/// The position of the camera.
enum CameraPosition { front, back }

/// Image inversion mode (to support barcodes in inverted colors).
/// The inversion is applied to the recognition stream only. Camera preview always stays unchanged.
/// Currently, only images of YUV_420_888 format can be inverted.
enum ImageInversion {

  /// All frames are kept as is. This is the default option.
  none,

  /// Every frame is inverted.
  invertAllFrames,

  /// Every 2nd frame is inverted. Useful if both inverted and not inverted barcodes should be recognized.
  alternateFrameInversion
}

/// The configuration by which the camera feed can be laid out in the UI.
class PreviewConfiguration {
  /// The width of the camera feed in points.
  final int width;

  /// The height of the camera feed in points.
  final int height;

  /// The orientation of the camera feed.
  final num sensorOrientation;

  /// A id of a texture which contains the camera feed.
  ///
  /// Can be consumed by a [Texture] widget.
  final int textureId;

  PreviewConfiguration(Map<dynamic, dynamic> response)
      : textureId = response["textureId"],
        sensorOrientation = response["surfaceOrientation"],
        height = response["surfaceHeight"],
        width = response["surfaceWidth"];
}
