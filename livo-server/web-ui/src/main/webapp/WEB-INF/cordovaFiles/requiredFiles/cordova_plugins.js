cordova.define('cordova/plugin_list', function(require, exports, module) {
               
    module.exports = [
                     {
                     "file": "plugins/cordova-plugin-whitelist/whitelist.js",
                     "id": "cordova-plugin-whitelist.whitelist",
                     "runs": true
                     },
                     {
                     "file": "plugins/tr.com.livo.plugin.serviceobjects/www/Livo.ServiceObjects.js",
                     "id": "tr.com.livo.plugin.serviceobjects.Livo.ServiceObjects",
                     "clobbers": [
                                  "Livo.ServiceObjects"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-network-information/www/network.js",
                     "id": "cordova-plugin-network-information.network",
                     "clobbers": [
                                  "navigator.connection",
                                  "navigator.network.connection"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-network-information/www/Connection.js",
                     "id": "cordova-plugin-network-information.Connection",
                     "clobbers": [
                                  "Connection"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-splashscreen/www/splashscreen.js",
                     "id": "cordova-plugin-splashscreen.SplashScreen",
                     "clobbers": [
                                  "navigator.splashscreen"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-actionsheet/www/ActionSheet.js",
                     "id": "cordova-plugin-actionsheet.ActionSheet",
                     "pluginId": "cordova-plugin-actionsheet",
                     "clobbers": [
                                  "window.plugins.actionsheet"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-camera/www/CameraConstants.js",
                     "id": "cordova-plugin-camera.Camera",
                     "pluginId": "cordova-plugin-camera",
                     "clobbers": [
                                  "Camera"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-camera/www/CameraPopoverOptions.js",
                     "id": "cordova-plugin-camera.CameraPopoverOptions",
                     "pluginId": "cordova-plugin-camera",
                     "clobbers": [
                                  "CameraPopoverOptions"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-camera/www/Camera.js",
                     "id": "cordova-plugin-camera.camera",
                     "pluginId": "cordova-plugin-camera",
                     "clobbers": [
                                  "navigator.camera"
                                  ]
                     },
                     {
                       "file": "plugins/cordova-plugin-camera/www/" + (currNav == "ios" ? currNav + "/" : "") + "CameraPopoverHandle.js",
                     "id": "cordova-plugin-camera.CameraPopoverHandle",
                     "pluginId": "cordova-plugin-camera",
                     "clobbers": [
                                  "CameraPopoverHandle"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-geolocation/www/Coordinates.js",
                     "id": "cordova-plugin-geolocation.Coordinates",
                     "pluginId": "cordova-plugin-geolocation",
                     "clobbers": [
                                  "Coordinates"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-geolocation/www/PositionError.js",
                     "id": "cordova-plugin-geolocation.PositionError",
                     "pluginId": "cordova-plugin-geolocation",
                     "clobbers": [
                                  "PositionError"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-geolocation/www/Position.js",
                     "id": "cordova-plugin-geolocation.Position",
                     "pluginId": "cordova-plugin-geolocation",
                     "clobbers": [
                                  "Position"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-geolocation/www/geolocation.js",
                     "id": "cordova-plugin-geolocation.geolocation",
                     "pluginId": "cordova-plugin-geolocation",
                     "clobbers": [
                                  "navigator.geolocation"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-device/www/device.js",
                     "id": "cordova-plugin-device.device",
                     "pluginId": "cordova-plugin-device",
                     "clobbers": [
                                  "device"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/DirectoryEntry.js",
                     "id": "cordova-plugin-file.DirectoryEntry",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.DirectoryEntry"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/DirectoryReader.js",
                     "id": "cordova-plugin-file.DirectoryReader",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.DirectoryReader"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/Entry.js",
                     "id": "cordova-plugin-file.Entry",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.Entry"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/File.js",
                     "id": "cordova-plugin-file.File",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.File"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileEntry.js",
                     "id": "cordova-plugin-file.FileEntry",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileEntry"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileError.js",
                     "id": "cordova-plugin-file.FileError",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileError"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileReader.js",
                     "id": "cordova-plugin-file.FileReader",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileReader"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileSystem.js",
                     "id": "cordova-plugin-file.FileSystem",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileSystem"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileUploadOptions.js",
                     "id": "cordova-plugin-file.FileUploadOptions",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileUploadOptions"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileUploadResult.js",
                     "id": "cordova-plugin-file.FileUploadResult",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileUploadResult"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/FileWriter.js",
                     "id": "cordova-plugin-file.FileWriter",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.FileWriter"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/Flags.js",
                     "id": "cordova-plugin-file.Flags",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.Flags"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/LocalFileSystem.js",
                     "id": "cordova-plugin-file.LocalFileSystem",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.LocalFileSystem"
                                  ],
                     "merges": [
                                "window"
                                ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/Metadata.js",
                     "id": "cordova-plugin-file.Metadata",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.Metadata"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/ProgressEvent.js",
                     "id": "cordova-plugin-file.ProgressEvent",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.ProgressEvent"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/fileSystems.js",
                     "id": "cordova-plugin-file.fileSystems",
                     "pluginId": "cordova-plugin-file"
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/requestFileSystem.js",
                     "id": "cordova-plugin-file.requestFileSystem",
                     "pluginId": "cordova-plugin-file",
                     "clobbers": [
                                  "window.requestFileSystem"
                                  ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/resolveLocalFileSystemURI.js",
                     "id": "cordova-plugin-file.resolveLocalFileSystemURI",
                     "pluginId": "cordova-plugin-file",
                     "merges": [
                                "window"
                                ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/browser/isChrome.js",
                     "id": "cordova-plugin-file.isChrome",
                     "pluginId": "cordova-plugin-file",
                     "runs": true
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/ios/FileSystem.js",
                     "id": "cordova-plugin-file.iosFileSystem",
                     "pluginId": "cordova-plugin-file",
                     "merges": [
                                "FileSystem"
                                ]
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/fileSystems-roots.js",
                     "id": "cordova-plugin-file.fileSystems-roots",
                     "pluginId": "cordova-plugin-file",
                     "runs": true
                     },
                     {
                     "file": "plugins/cordova-plugin-file/www/fileSystemPaths.js",
                     "id": "cordova-plugin-file.fileSystemPaths",
                     "pluginId": "cordova-plugin-file",
                     "merges": [
                                "cordova"
                                ],
                     "runs": true
                      },
                      {
                      "file": "plugins/cordova-plugin-file-transfer/www/FileTransferError.js",
                      "id": "cordova-plugin-file-transfer.FileTransferError",
                      "clobbers": [
                                   "window.FileTransferError"
                                   ]
                      },
                      {
                      "file": "plugins/cordova-plugin-file-transfer/www/FileTransfer.js",
                      "id": "cordova-plugin-file-transfer.FileTransfer",
                      "clobbers": [
                                   "window.FileTransfer"
                                   ]
                      }
    ];
    module.exports.metadata =
    {
        "cordova-plugin-whitelist": "1.0.0",
        "tr.com.livo.plugin.serviceobjects": "1.0.0",
        "cordova-plugin-network-information": "1.0.2-dev",
        "cordova-plugin-splashscreen": "2.1.1-dev",
        "cordova-plugin-actionsheet": "2.3.3",
        "cordova-plugin-compat": "1.2.0",
        "cordova-plugin-camera": "2.4.1",
        "cordova-plugin-geolocation": "2.4.3",
        "cordova-plugin-device": "1.1.6",
        "cordova-plugin-file": "4.3.3",
        "cordova-plugin-file-transfer": "1.6.3",
        "cordova-sqlite-storage": "2.0.4",
        "phonegap-plugin-barcodescanner": "6.0.8",
        "cordova-plugin-media-capture": "1.4.3"
    }

});