package com.alibaba.cloud.alifaceenginedemo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import com.alibaba.cloud.faceengine.Error;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class Utils {
    public static String filePath = Environment.getExternalStorageDirectory() + "/com.alibaba.cloud.alifaceenginedemo/";

    public static native void displayNV21ToBitmap(Bitmap bitmap, byte[] yuv, int width, int height);

    static {
        System.loadLibrary("native-lib");
    }

    public static String getError(int error) {
        if (error == Error.OK) {
            return "OK";
        }
        if (error == Error.FAILED) {
            return "FAILED";
        }
        if (error == Error.ERROR_EXPIRE) {
            return "ERROR_EXPIRE";
        }
        if (error == Error.ERROR_AUTH_FAIL) {
            return "ERROR_AUTH_FAIL";
        }
        if (error == Error.ERROR_INVALID_ARGUMENT) {
            return "ERROR_INVALID_ARGUMENT";
        }
        if (error == Error.ERROR_DB_EXEC) {
            return "ERROR_DB_EXEC";
        }
        if (error == Error.ERROR_EXISTED) {
            return "ERROR_EXISTED";
        }
        if (error == Error.ERROR_NOT_EXIST) {
            return "ERROR_NOT_EXIST";
        }
        if (error == Error.ERROR_NETWORK_FAIL) {
            return "ERROR_NETWORK_FAIL";
        }
        if (error == Error.ERROR_NETWORK_RECV_JSON_WRONG) {
            return "ERROR_NETWORK_RECV_JSON_WRONG";
        }
        if (error == Error.ERROR_NO_FACE) {
            return "ERROR_NO_FACE";
        }
        if (error == Error.ERROR_FORMAT_NOT_SUPPORT) {
            return "ERROR_FORMAT_NOT_SUPPORT";
        }
        if (error == Error.ERROR_NO_ID) {
            return "ERROR_NO_ID";
        }
        if (error == Error.ERROR_CLOUD_OK) {
            return "ERROR_CLOUD_OK";
        }
        if (error == Error.ERROR_CLOUD_ACCOUT_WRONG) {
            return "ERROR_CLOUD_ACCOUT_WRONG";
        }
        if (error == Error.ERROR_CLOUD_REQUEST_DATA_ERROR) {
            return "ERROR_CLOUD_REQUEST_DATA_ERROR";
        }
        if (error == Error.ERROR_CLOUD_DB_EXEC_ERROR) {
            return "ERROR_CLOUD_DB_EXEC_ERROR";
        }
        if (error == Error.ERROR_CLOUD_EXISTED_ERROR) {
            return "ERROR_CLOUD_EXISTED_ERROR";
        }
        if (error == Error.ERROR_CLOUD_NOT_EXIST_ERROR) {
            return "ERROR_CLOUD_NOT_EXIST_ERROR";
        }
        if (error == Error.ERROR_CLOUD_NO_AUTHORIZE) {
            return "ERROR_CLOUD_NO_AUTHORIZE";
        }
        if (error == Error.ERROR_CLOUD_ALGORITHOM_ERROR) {
            return "ERROR_CLOUD_ALGORITHOM_ERROR";
        }
        if (error == Error.ERROR_CLOUD_NO_FACE) {
            return "ERROR_CLOUD_NO_FACE";
        }
        if (error == Error.ERROR_CLOUD_FAILED) {
            return "ERROR_CLOUD_FAILED";
        }
        if (error == Error.ERROR_CLOUD_NOT_SUPPORT) {
            return "ERROR_CLOUD_NOT_SUPPORT";
        }
        return "UNKOWN ERROR";
    }

    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount(); // 返回可用于储存此位图像素的最小字节数
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes);//使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(byteBuffer);//将位图的像素复制到制定缓冲区
        byte[] rgba = byteBuffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];
        int count = rgba.length / 4;
        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {
            pixels[i * 3] = rgba[i * 4];     //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1]; //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2]; //B
        }
        return pixels;
    }

    public static Bitmap Bytes2Bimap(byte[] b, Camera camera) {
        if (b.length != 0) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            YuvImage yuvImage = new YuvImage(b, ImageFormat.NV21, size.width, size.height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, baos);
            byte[] jdata = baos.toByteArray();
            return BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
        } else {
            return null;
        }
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager manager = context.getResources().getAssets();
        try {
            InputStream inputStream = manager.open(fileName);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 删除指定系统文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    //     读取照片exif信息中的旋转角度
//
//     @return角度 获取从相册中选中图片的角度
//
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return degree;
    }

    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        //&& Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static byte[] loadFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("file not exist:" + filePath);
            return null;
        }

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }

        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        try {
            FileInputStream fi = new FileInputStream(file);
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            fi.close();
        } catch (IOException e) {
        }

        if (offset != buffer.length) {
            System.out.println("Could not completely read file");
            return null;
        }

        return buffer;
    }
}
