package com.cross.juntalk2.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {


    public static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    // todo : System.Os.Property set
    public static void set(String key, String val) throws IllegalArgumentException {
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = {String.class, String.class};
            Method set = SystemProperties.getMethod("set", paramTypes);
            Object[] params = {key, val};
            set.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("abc", "IllegalArgumentException e: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("abc", "IllegalArgumentException e: " + e.toString());
        }
    }
    // todo : System.Os.Property  get
    public static String get(String key) {
        String ret = "";
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = {String.class};
            Method get = SystemProperties.getMethod("get", paramTypes);
            Object[] params = {key};
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            ret = "";
            e.printStackTrace();
            Log.e("abc", "IllegalArgumentException e: " + e.toString());
        } catch (Exception e) {
            ret = "";
            e.printStackTrace();
            Log.e("abc", "Exception e: " + e.toString());
        }
        return ret;
    }


    public static void resetApp(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.clearApplicationUserData();
    }

    //앱 및 패키지 설치 여부 확인
    public static boolean getInstallPackage(String packagename, Context context) {
        String mpackagename = packagename;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mpackagename.trim(), PackageManager.GET_META_DATA);
            ApplicationInfo appInfo = pi.applicationInfo;
            // 패키지가 있을 경우.
            Log.d("abc", "Enabled value = " + appInfo.enabled);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("abc", "패키지가 설치 되어 있지 않습니다.");
            return false;
        }
    }


    /*
     * String형을 BitMap으로 변환시켜주는 함수
     * */
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /*
     * Bitmap을 String형으로 변환
     * */
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    /*
     * Bitmap을 byte배열로 변환
     * */
    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return baos.toByteArray();
    }


    public static Bitmap getImageFromURL(String imageURL) {
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }

    /**
     * 获取小于api19时获取相册中图片真正的uri
     * 对于路径是：content://media/external/images/media/33517这种的，需要转成/storage/emulated/0/DCIM/Camera/IMG_20160807_133403.jpg路径，也是使用这种方法
     *
     * @param context
     * @param uri
     * @return
     */
    private static String getFilePath_below19(Context context, Uri uri) {
        Cursor cursor = null;
        String path = "";
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    // Bitmap을 Byte로 변환
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    // Byte를 Bitmap으로 변환
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        // FIXME: 2018/11/13 Added if condition for test
        if (uri != null) {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            String pathHead = "";
            // 1. DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // 1.1 ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // 1.2 DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    return id.substring(4);
                }
                // 1.3 MediaProvider
                else if (isMediaDocument(uri)) {
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

                    return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // 2. MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                } else if (isGooglePlayPhotosUri(uri)) {
                    return getImageUrlWithAuthority(context, uri);
                } else {
                    return getFilePath_below19(context, uri);
                }
            }
            // 3. 判断是否是文件形式 File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return pathHead + uri.getPath();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGooglePlayPhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Log.i("err getImageUrl...() : ", e.toString());
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.i("err getImageUrl...() : ", e.toString());
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
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


}

