package com.developer.fabiano.ifprof.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtil {
	public static Bitmap setPic(String pathFile, int width, int height) {
	    // Get the dimensions of the View
		if (width == 0){
			width = 100;
		}
		if (height == 0){
			height = 100;
		}
		    int targetW = width;
		    int targetH = height;
	    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(pathFile, bmOptions);

		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;

	    Bitmap bitmap = BitmapFactory.decodeFile(pathFile, bmOptions);
	    return(bitmap);
	}
	public static Uri handleImageUri(Uri uri) {
		Pattern pattern = Pattern.compile("(content://media/.*\\d)");
		if (uri.getPath().contains("content")) {
			Matcher matcher = pattern.matcher(uri.getPath());
			if (matcher.find())
				return Uri.parse(matcher.group(1));
			else
				throw new IllegalArgumentException("Cannot handle this URI");
		} else
			return uri;
	}
	public static String getRealPathFromURI(Context context, Uri uri) {
		Cursor cursor = null;
		try {
			Uri newUri = handleImageUri(uri);
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(newUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e){
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
