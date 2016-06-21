package com.tvpal.kobi.tvpal.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Kobi on 04/06/2016.
 */
public class ModelCloudinary {
    Cloudinary cloudinary;

    public ModelCloudinary(){
        cloudinary = new Cloudinary("cloudinary://844994179122988:AlH1hJcUEXGYSd66LZeRTr4kqcI@tvpal");
    }

    public void saveImage(final Bitmap imageBitmap, final String imageName) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                    String name = (imageName.contains("."))? imageName.substring(0,imageName.lastIndexOf(".")):imageName;
                    Map res = cloudinary.uploader().upload(bs , ObjectUtils.asMap("public_id", name));
                    Log.d("TAG","save image to url" + res.get("url"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    public Bitmap loadImage(String imageName) {
        URL url = null;
        try {
            Log.d("TAG","Bring from cloudinary");
            url = new URL(cloudinary.url().generate(imageName));
            Log.d("TAG", "load image from url" + url);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "url" + url);
        return null;
    }
    
}
