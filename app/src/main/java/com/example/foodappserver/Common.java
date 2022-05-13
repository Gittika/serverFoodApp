package com.example.foodappserver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.foodappserver.Remote.IGeoCordinates;
import com.example.foodappserver.Remote.RetrofitClient;

public class Common {
   public static User currentUser;
   public static  Request currentRequest;


   public  static  final  String UPDATE ="Update";
   public  static  final  String DELETE ="Delete";


   public   static  final int PICK_IMAGE_REQUEST =71;

   public static   String convertCodeToStatus(String status){
      if(status.equals("0"))
         return "Placed";
      else if(status.equals("1"))
         return "On the way";
      else
         return  "Shipped";
   }

   public  static  final  String baseUrl ="https://maps.googleapis.com";

   public static IGeoCordinates getGeoCodeServices(){

   return RetrofitClient.getClient(baseUrl).create(IGeoCordinates.class);

}

public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
{
   Bitmap scaleBitmap =Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

   float scaleX =newWidth/(float)bitmap.getWidth();
   float scaleY=newHeight/(float)bitmap.getHeight();
   float pivotX=0,pivotY=0;

   Matrix scaleMatrix =new Matrix()
;
   scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

   Canvas canvas =new Canvas(scaleBitmap);
   canvas.setMatrix(scaleMatrix);
   canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
   return scaleBitmap;



}

}
