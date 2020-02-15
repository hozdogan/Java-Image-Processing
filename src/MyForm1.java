 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.Videoio;




public class MyForm1 extends javax.swing.JFrame {

class Dnnobject
{
     private int objectClassId;
     
     private Point Startpos;
     private Point finishpos;
     private String objename;
     public Dnnobject(int objectid,String objename,Point start,Point bitis)
     {
         objectClassId=objectid;
         Startpos=start;
         finishpos=bitis;
         this.objename=objename;
         
     }
    
    
}
    
class İmageManipulation
{
   
    private Net net;
    private final String proto = "C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\sources\\modules\\dnn\\src\\caffe\\MobileNetSSD_deploy.prototxt";//orjinal MobileNetSSD_deploy.prototxt ;bvlc_googlenet_deploy
    private final String model = "C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\sources\\modules\\dnn\\src\\caffe\\MobileNetSSD_deploy.caffemodel";
    private final String[] classNames = {"background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"};
    private int width,heigth;
    public BufferedImage image;
    public int thres=2;
    int x1,y1,x2,y2,crop_w,crop_h;
    public String opencvpath="C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll";
    public boolean running=false;
    public VideoCapture cam;
    
    
    protected String fpath="C:\\Users\\asus\\Documents\\NetBeansProjects\\Goruntu\\src\\imgproc\\";//işlenmiş resimlerin koyuldugu yer
    protected String facepath="C:\\Users\\asus\\Documents\\NetBeansProjects\\Goruntu\\src\\faceimg\\";
    public void getpixel()//learning function bu proje için 
        {
            
            System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
            Mat image = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
            Mat newimg = new Mat(image.rows(),image.cols(),image.type());
            MatOfByte mob = new MatOfByte();
            if(Imgcodecs.imencode(".jpg", image, mob))// 1 noktayı görmedim 2 saattir arıyorum olmuyor
            {
                byte [] pixelarray = mob.toArray();//su an bgr bgr degil
                double [] rgb = image.get(20, 20);//3 elemanlı bir dizi
                
                for(int row=0;row<image.rows();row++)
                {
                for(int col=0;col<image.cols();col++)
                {
                    newimg.put(row, col, image.get(row, col));
                    double [] pixels=image.get(row, col);
                    for(int i=0;i<pixels.length;i++)
                    {
                        System.out.println(pixels[i]);//pixels sürekli dinamik değişiyo elemanları ve yazılıyor her pixel 3 boyutlu bir dizi rgb 
                    }
                    
                }
                }
                for(int i=0;i<rgb.length;i++)
                {
                    System.out.println(rgb[i]);
                }
                System.out.println("length = "+pixelarray.length+"boyut = "+image.cols()*image.rows());
                //System.out.println(pixelarray[i]);
                //bu byte dizisine dönüşmüş veriyi alıp bufferedimage de type 3byte_bgr şeklinde oluşturup c++ daki byte dizileri gibide resmi okuyabiliriz bu daha kolay
                
            }
            
        }
    
    public BufferedImage GrayScale()
    {
        
        try
        {
            File f = new File(filepath);
            image=ImageIO.read(f);
            
            width=image.getWidth();
            heigth=image.getHeight();
            for(int row=0;row<heigth;row++) 
            {
                for(int col=0;col<width;col++)
                {
                    Color c = new Color(img.getRGB(col, row));
                    //int val=c.getRGB();//- değerler cıkıyo 32 bit sayı en anlamlı 8 alpha degeri diğerleri r g b sırayla
                    //System.out.println(val>>16);
                    int red = (int)(c.getRed() * 0.299); 
                    int green = (int)(c.getGreen() * 0.587); 
                    int blue = (int)(c.getBlue() *0.114);
                    int sum=red+green+blue;
                     Color newColor = new Color(sum,sum,sum);
                     image.setRGB(col, row, newColor.getRGB());
                     /*int val=newColor.getRGB();//16 sağa kaydır bit elde ediyoruz gri pixeli color tanımlamaya gerek yok
                     int vall=val>>16;
                     if(vall<0)
                     {
                         vall+=256;
                     }
                     Color vl = new Color(vall,vall,vall);
                     image.setRGB(col,row, vl.getRGB());*/
                     
                }
            }
            String path=fpath+"grayscale.jpg";
            File out = new File(path);
            ImageIO.write(image, "jpg", out);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return image;
    }
    public int [] histogram()
    {
        int [] Hist = new int [256];
        for(int i=0;i<256;i++)
        {
            Hist[i]=0;
        }
        try 
        {
            File f = new File(filepath);
            image=ImageIO.read(f);
            width=image.getWidth();
            heigth=image.getHeight();
            for(int row=0;row<heigth;row++)
            {
                for(int col=0;col<width;col++)
                {
                    Color c = new Color(image.getRGB(col, row));
                    int val=c.getRed()+c.getBlue()+c.getGreen();
                    Hist[val/3]++;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return Hist;
    }
    public BufferedImage BinaryImage() 
    {
        BufferedImage gray = GrayScale();//ana dosyayı açıp okuyor grayscale i bize veriyor zaten
        int [] dizi = new int [256];
        dizi=histogram();
        int T1,T2,T1comp,T2comp,sumt1=0,sumt1px=0,sumt2=0,sumt2pix=0,Mean;
        Random rn = new Random();
        T1=rn.nextInt(256);
        T2=rn.nextInt(256);
        if(T1==T2)
        {
            T1=rn.nextInt(256);
            T2=rn.nextInt(256);
        }
        while(true)
        {
            Mean=(T1+T2)/2;
            for(int i=0;i<Mean;i++)
            {
                sumt1+=dizi[i]*i;
                sumt1px+=dizi[i];
            }
            T1comp=sumt1/sumt1px;
            for(int j=Mean;j<256;j++)
            {
                sumt2+=dizi[j]*j;
                sumt2pix+=dizi[j];
            }
            T2comp=sumt2/sumt2pix;
            if(T1==T1comp&&T2==T2comp)
            {
                thres=Mean;
                break;
            }
            else
            {
                Mean=0;sumt1=0;sumt1px=0;
                sumt2=0;sumt2pix=0;
                T1=T1comp;T2=T2comp;
                T2comp=0;T1comp=0;
            }
        }
        
        width=gray.getWidth();
        heigth=gray.getHeight();
        for(int row=0;row<heigth;row++)
        {
            for(int col=0;col<width;col++)
            {
                Color c = new Color(gray.getRGB(col, row));
                Color black = new Color(0,0,0);
                Color white = new Color(255,255,255);
                int sum=(c.getRed()+c.getBlue()+c.getGreen())/3;
                if(sum<Mean)
                {
                    gray.setRGB(col, row, black.getRGB());
                }
                else if(sum>=Mean)
                {
                    gray.setRGB(col, row, white.getRGB());
                }
                
            }
        }
        String path=fpath+"binary.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(gray, "jpg", out);
        }
        catch(Exception e){e.printStackTrace();}
        return gray;
    }
    public BufferedImage Dilation()
    {
        
        BufferedImage binary=BinaryImage();
        BufferedImage bin = new BufferedImage(binary.getWidth(), binary.getHeight(),BufferedImage.TYPE_INT_RGB);
        int w_new=binary.getWidth()-2,h_new=binary.getHeight()-2;
        BufferedImage dilate = new BufferedImage(binary.getWidth(), binary.getHeight(),BufferedImage.TYPE_INT_RGB);
        Color bl = new Color(1,1,1);
        Color black = new Color(0,0,0);
        Color white = new Color(255,255,255);
        for(int row=0;row<binary.getHeight();row++)//siyahsa 1 beyazsa 0 sonra göstermek için 0 255 yapılacak
        {
            for(int col=0;col<binary.getWidth();col++)
            {
                Color c = new Color(binary.getRGB(col, row));
                if(c.getRGB()==black.getRGB())
                {
                    binary.setRGB(col, row, bl.getRGB());
                }
                else if(c.equals(white))
                {
                    binary.setRGB(col, row, black.getRGB());
                }
            }
        }
        
        int [][] mask={{1,1,1},{1,1,1},{1,1,1}};
        for(int i=0;i<h_new;i++)
        {
            for(int j=0;j<w_new;j++)
            {
                for(int a=i;a<i+3;a++)
                {
                    for(int b=j;b<j+3;b++)
                    {
                        
                        Color tpp = new Color(binary.getRGB(b, a));
                        if(binary.getRGB(j+1, i+1)==bl.getRGB())
                        {
                            int val1=tpp.getRed();
                            int rs = val1+mask[a-i][b-j];
                            if(rs>1){rs=1;}
                            if(rs==1)
                            {
                                dilate.setRGB(b, a, bl.getRGB());
                            }
                            else if(rs==0)
                            {
                                dilate.setRGB(b, a, black.getRGB());
                            }
                              
                        }
                       
                    }
                }
            }
        }//or
       
        for(int row=0;row<h_new;row++)
        {
            for(int col=0;col<w_new;col++)
            {
                if(dilate.getRGB(col, row)==bl.getRGB())
                {
                    dilate.setRGB(col, row, black.getRGB());
                }
                else if(dilate.getRGB(col, row)==black.getRGB())
                {
                    dilate.setRGB(col, row, white.getRGB());
                }
            }
        }
        try
        {
           String path=fpath+"dilate.jpg";
           File out = new File(path);
           ImageIO.write(dilate, "jpg", out);
           
        }catch(Exception e){e.printStackTrace();}
        return dilate;
    }
    public BufferedImage Erosion(){
        
        BufferedImage binary=BinaryImage();
       
        int w_new=binary.getWidth()-2,h_new=binary.getHeight()-2;
        BufferedImage erosion = new BufferedImage(binary.getWidth(), binary.getHeight(),BufferedImage.TYPE_INT_RGB);
        Color bl = new Color(1,1,1);
        Color black = new Color(0,0,0);
        Color white = new Color(255,255,255);
        for(int row=0;row<binary.getHeight();row++)//siyahsa 1 beyazsa 0 sonra göstermek için 0 255 yapılacak
        {
            for(int col=0;col<binary.getWidth();col++)
            {
                Color c = new Color(binary.getRGB(col, row));
                if(c.getRGB()==black.getRGB())
                {
                    binary.setRGB(col, row, bl.getRGB());
                }
                else if(c.equals(white))
                {
                    binary.setRGB(col, row, black.getRGB());
                }
            }
        }
        
        for(int i=0;i<binary.getHeight();i++)
        {
            for(int j=0;j<binary.getWidth();j++)
            {
                erosion.setRGB(j, i, black.getRGB());
            }
        }
        
        int [][] mask={{1,1,1},{1,1,1},{1,1,1}};
        int count=0,numberofonemask=0;
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(mask[i][j]==1)
                    numberofonemask++;
            }
        }
        
        for(int i=0;i<h_new;i++)
        {
            for(int j=0;j<w_new;j++)
            {
                for(int a=i;a<i+3;a++)
                {
                    for(int b=j;b<j+3;b++)
                    {
                        Color tpp = new Color(binary.getRGB(b, a));
                        int val=tpp.getRed();
                        if(mask[a-i][b-j]==1&&val==1)
                        {
                            count++;
                        }
                    }
                }
                if(count>=numberofonemask)
                {
                    erosion.setRGB(j+1, i+1, bl.getRGB());
                    count=0;
                }
                else
                {
                    erosion.setRGB(j+1, i+1, black.getRGB());
                    count=0;
                }   
            }
        }//or
       
        for(int row=0;row<h_new;row++)
        {
            for(int col=0;col<w_new;col++)
            {
                if(erosion.getRGB(col, row)==bl.getRGB())
                {
                    erosion.setRGB(col, row, black.getRGB());
                }
                else if(erosion.getRGB(col, row)==black.getRGB())
                {
                    erosion.setRGB(col, row, white.getRGB());
                }
            }
        }
        try
        {
           String path=fpath+"erote.jpg";
           File out = new File(path);
           ImageIO.write(erosion, "jpg", out);
           
        }catch(Exception e){e.printStackTrace();}
        return erosion;
        
        
       
    }
    public BufferedImage Boundary()
    {
        BufferedImage img1 =BinaryImage();
        BufferedImage img2=Erosion();
        BufferedImage boundary = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
        Color bl = new Color(1,1,1);
        Color black = new Color(0,0,0);
        Color white = new Color(255,255,255);
        
         for(int row=0;row<img1.getHeight();row++)//siyahsa 1 beyazsa 0 sonra göstermek için 0 255 yapılacak
        {
            for(int col=0;col<img1.getWidth();col++)
            {
                Color c = new Color(img1.getRGB(col, row));
                Color c2 = new Color(img2.getRGB(col, row));
                if(c.equals(white))
                {
                    img1.setRGB(col, row, bl.getRGB());
                }
                if(c2.equals(white))
                {
                    img2.setRGB(col, row, bl.getRGB());
                }
            }
        }
        
        for(int row=0;row<boundary.getHeight();row++)
        {
            for(int col=0;col<boundary.getWidth();col++)
            {
                int val1=0,val2=0,val3;
                Color c1 = new Color(img1.getRGB(col, row));
                Color c2 = new Color(img2.getRGB(col, row));
                val1=c1.getRed();val2=c2.getRed();val3=val2^val1;
                if(val3==1)
                {
                    boundary.setRGB(col, row, white.getRGB());
                }
                else if(val3==0)
                {
                    boundary.setRGB(col, row, black.getRGB());
                }
                
            }
        }
        String path=fpath+"boundary.jpg";
        File out = new File(path);
        try{
            ImageIO.write(boundary,"jpg",out);
        }catch(Exception e){e.printStackTrace();}
        
        
        return boundary;
    }
    
    public BufferedImage Canny_Edge()
    {
         BufferedImage edge=GrayScale();int sum=0;
         int w_new=edge.getWidth()-2,h_new=edge.getHeight()-2;
         BufferedImage edgevertical = new BufferedImage(w_new, h_new, BufferedImage.TYPE_INT_RGB);
         BufferedImage edgehorizontal = new BufferedImage(w_new, h_new, BufferedImage.TYPE_INT_RGB);
         BufferedImage canny = new BufferedImage(w_new, h_new, BufferedImage.TYPE_INT_RGB);
         int [][] prewittvertical={{1,2,1},{0,0,0},{-1,-2,-1}};
         int [][] prewitthorizontal ={{-1,0,1},{-2,0,2},{-1,0,1}};
         for(int i=0;i<h_new;i++)
        {
            for(int j=0;j<w_new;j++)
            {
                for(int a=i;a<i+3;a++)
                {
                    for(int b=j;b<j+3;b++)
                    {
                       Color c = new Color(edge.getRGB(b, a));
                       sum+=c.getRed()*prewittvertical[a-i][b-j];//prewitt vertical ise edge horizontal ters yonde türev alma
                    }
                }
                int pix=Math.abs(sum)/4;
                Color hor = new Color(pix,pix,pix);
                edgehorizontal.setRGB(j, i, hor.getRGB());
                sum=0;
            }
        }//or
        //dikey yönde türev alalım şimdide
        for(int i=0;i<h_new;i++)
        {
            for(int j=0;j<w_new;j++)
            {
                for(int a=i;a<i+3;a++)
                {
                    for(int b=j;b<j+3;b++)
                    {
                       Color c = new Color(edge.getRGB(b, a));
                       sum+=c.getRed()*prewitthorizontal[a-i][b-j];//prewitt vertical ise edge horizontal ters yonde türev alma
                    }
                }
                int pix2=Math.abs(sum)/4;
                Color ver = new Color(pix2,pix2,pix2);
                edgevertical.setRGB(j, i, ver.getRGB());
                sum=0;
            }
        }//or
        for(int row=0;row<canny.getHeight();row++)
        {
            for(int col=0;col<canny.getWidth();col++)
            {
                Color edge1= new Color(edgehorizontal.getRGB(col, row));
                Color edge2 = new Color(edgevertical.getRGB(col, row));
                int toplam=(edge1.getRed()+edge2.getRed());
                if(toplam>255)
                {
                    toplam/=4;
                }
                Color edge3 = new Color(toplam,toplam,toplam);
                canny.setRGB(col, row, edge3.getRGB());
            }
        }
         
        String path=fpath+"cannyedge.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(canny, "jpg", out);
        }catch(Exception e){e.printStackTrace();}
        
        return canny;
        
    }
    public BufferedImage InverseImage()
    {
        BufferedImage can = Canny_Edge();
        width = can.getWidth();
        heigth = can.getHeight();
        BufferedImage imge = new BufferedImage(width, heigth, can.getType());
        for(int row=0;row<heigth;row++)
        {
            for(int col=0;col<width;col++)
            {
                Color c = new Color(can.getRGB(col, row));
                int newred=255-c.getRed();
                int newgreen = 255-c.getGreen();
                int newblue = 255-c.getBlue();
                Color a = new Color(newred,newgreen,newblue);
                imge.setRGB(col,row, a.getRGB());
            }
        }
        String path=fpath+"inverse.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(imge, "jpg", out);
        }catch(Exception e){e.printStackTrace();}
        
        return imge;
    }
    public BufferedImage DFT()
    {
        BufferedImage gray =GrayScale();
        
        
        int uf=gray.getHeight(),vf=gray.getWidth();
        int u=gray.getHeight(),v=gray.getWidth();
        BufferedImage spectial = new BufferedImage(v,u,BufferedImage.TYPE_INT_RGB);
        Color spec;
        double euclid;
        double re=0,im=0,a,b,pix;
        int eclid;
        
    
        for(int i=0;i<u;i++)
        {
            for(int j=0;j<v;j++)//row veya col index bi şeyle çarpılacaksa float yapılmaz 1 1.1 1.11 diye artar sonsuza gider runtime bug bölünen sayıyı float yap veya sonucu onun yerine
            {
                
                for(int row=0;row<gray.getHeight();row++)//aklında tut kesirli bir şey varsa int herşeyi for içi bile olsa float yap yada her değişkene teker teker bak
                {
                    for(int col=0;col<gray.getWidth();col++)
                    {
                        Color c = new Color(gray.getRGB(col,row));
                       a=(i*row)/uf;
                       b=(j*col)/vf;
                       pix=c.getRed();
                       re+=pix*Math.cos((2*Math.PI)*(a+b));//u,v koordintındaki frekans bilgisi
                       im+=pix*(Math.sin((2*Math.PI)*(a+b))*-1);
                       
                    }
                }
                re/=(uf*vf);//discrete de bu var bu olmasa bilgi sıkışı frekans da olur bir pixel 1900 değerli gibi buyuk olur
                im/=(uf*vf);
                euclid=Math.sqrt(Math.pow(re,2)+Math.pow(im,2));
                
               // System.out.println("ilk uzaklık : "+euclid);
                
                eclid=(int)euclid;
                spec = new Color(eclid,eclid,eclid);
                spectial.setRGB(j,i,spec.getRGB());
                //System.out.println("pixel değeri : "+spec.getBlue());
                re=0.0;
                im=0.0;
                euclid=0.0;
                
            }
        }
            
        
        String path=fpath+"dft.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(spectial, "jpg", out);
        }catch(Exception e){e.printStackTrace();}
        
        
        return spectial;
        
    }
    public BufferedImage LogPixel()
    {
        BufferedImage gray = GrayScale();
        BufferedImage spectial = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_INT_RGB);
        int maxbrightness=0;
        double coeff;
         for(int row=0;row<gray.getHeight();row++)
         {
            for(int col=0;col<gray.getWidth();col++)
            {
                Color c= new Color(gray.getRGB(col, row));
                maxbrightness=gray.getRGB(0, 0);
                if(c.getRGB()>maxbrightness)
                {
                    maxbrightness=c.getRGB();
                }
                
                
            }
         }
         coeff=255/Math.log10(1+Math.abs(maxbrightness));
         for(int row=0;row<gray.getHeight();row++)
         {
             for(int col=0;col<gray.getWidth();col++)
             {
                 Color cp = new Color(gray.getRGB(col,row));
                 double pixx=coeff*Math.log10(1+Math.abs(cp.getGreen()));
                 int pixxint=(int)pixx;
                 Color pix = new Color(pixxint,pixxint,pixxint);
                 spectial.setRGB(col, row, pix.getRGB());
             }
         }
         String path=fpath+"logpixel.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(spectial, "jpg", out);
        }catch(Exception e){e.printStackTrace();}           
        
        
        return spectial;
    }
    
    public BufferedImage DCT()
    {
        BufferedImage gray = GrayScale();
        
        double v=gray.getWidth(),u=gray.getHeight();
        double m=gray.getHeight(),n=gray.getWidth();
        BufferedImage spectial = new BufferedImage((int)v,(int)u,BufferedImage.TYPE_INT_RGB);
        Color spec;
        double a,b,ci,cj,sum=0;
        int eclid;
        
        for(int i=0;i<u;i++)
        {
            for(int j=0;j<v;j++)//birden cok noktanın frekansı
            {
                if (i == 0)
            {
                ci = 1 / Math.sqrt(m);
            }      
            else
            {
                ci = (Math.sqrt(2)/Math.sqrt(m)); 
            }   
            if (j == 0) 
            {
                cj = 1 / Math.sqrt(n); 
            }
            else
            {
                cj =(Math.sqrt(2)/Math.sqrt(n));
            }
            // sum will temporarily store the sum of  
            // cosine signals 
                sum=0.0; 
                for(int row=0;row<gray.getHeight();row++)
                {
                    for(int col=0;col<gray.getWidth();col++)
                    {
                        Color c = new Color(gray.getRGB(col,row));
                        a=i*(2*row+1)/(2*m);
                        b=j*(2*col+1)/(2*n);
                        sum+=Math.cos(Math.PI*a)*(Math.cos(Math.PI*b))*c.getRed();
                    }
                }
                eclid=(int)(sum*ci*cj);
                //System.out.println(euclid);
                eclid=Math.abs(eclid);
                if(eclid>255)
                {
                    eclid=eclid%256;
                    
                }
                /*if(eclid>1)//hpf atınca resim ortaya cıkıyor gerek yok buna
                {
                    eclid=128;
                }   */    
                        
                        
                spec = new Color(eclid,eclid,eclid);
                spectial.setRGB(j,i,spec.getRGB());
                
               
            }
        }

        String path=fpath+"dct.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(spectial, "jpg", out);
        }catch(Exception e){e.printStackTrace();}
        
        
        return spectial;
        
        
    }
    
    public BufferedImage exhaustive_method_for_image_forgery(int r, int c)//int r, int c
    {
        
        BufferedImage gray = GrayScale();
        
        int k=r,l=c,w=gray.getWidth(),h=gray.getHeight(),newr,newc;
        BufferedImage temp = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        
     
        for(int row=0;row<h;row++)
        {
            for(int col=0;col<w;col++)
            {
                Color pix = new Color(gray.getRGB(col, row));
                newr=(row+k)%h;//ters koymusuz doğrusu böle miş
                newc=(col+l)%w;
                
                Color secondpix= new Color(gray.getRGB(newc, newr));
                int np=Math.abs(pix.getRed()-secondpix.getRed());
                Color newpix = new Color(np,np,np);
                temp.setRGB(col, row,newpix.getRGB());
                
            }
        }
        String path=fpath+"exhaustforgery.jpg";
        File out = new File(path);
        try
        {
            ImageIO.write(temp, "jpg", out);
        }catch(Exception e){e.printStackTrace();}
        
        
        return temp;
    }
    
    public BufferedImage Different() throws IOException
    {
        
        String diffpath=fpath+"exhaustforgery.jpg";
        System.load(opencvpath);
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Mat source2 = Imgcodecs.imread(diffpath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Core.absdiff(source, source2, dest);
        Mat thresholdCikti=new Mat(); 
        Imgproc.threshold(dest, thresholdCikti, 2, 255, Imgproc.THRESH_BINARY); 
        String path=fpath+"diff.jpg";
        Imgcodecs.imwrite(path, thresholdCikti);
        File out = new File(path);
        BufferedImage img =ImageIO.read(out);
        return img;
        
    }
    
    public BufferedImage HPF() throws Exception
    {
        
        int kernelSize=5;
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);//color yaparsak renkli türev alır
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        
        Mat kernel = new Mat(kernelSize,kernelSize,CvType.CV_32F)
        {
            {
                put(0,0,1);
                put(0,1,1);
                put(0,2,1);
                put(0,3,1);
                put(0,4,1);
                
                put(1,0,1);
                put(1,1,1);
                put(1,2,1);
                put(1,3,1);
                put(1,4,1);
                
                put(2,0,1);
                put(2,1,1);
                put(2,2,0);
                put(2,3,1);
                put(2,4,1);
                
                put(3,0,1);
                put(3,1,1);
                put(3,2,1);
                put(3,3,1);
                put(3,4,1);
                
                put(4,0,1);
                put(4,1,1);
                put(4,2,1);
                put(4,3,1);
                put(4,4,1);
                
            }
        };
        
        Imgproc.filter2D(source, dest,-1,kernel);
        String path=fpath+"hpf.jpg";
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
       
        return img;
    }
    
    public BufferedImage LPF() throws Exception
    {
        
        int kernelSize=5;
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);//color yaparsak renkli türev alır
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize,CvType.CV_32F)
        {
            {
                put(0,0,0);
                put(0,1,0);
                put(0,2,0);
                put(0,3,0);
                put(0,4,0);
                
                put(1,0,0);
                put(1,1,0);
                put(1,2,0);
                put(1,3,0);
                put(1,4,0);
                
                put(2,0,0);
                put(2,1,0);
                put(2,2,1);
                put(2,3,0);
                put(2,4,0);
                
                put(3,0,0);
                put(3,1,0);
                put(3,2,0);
                put(3,3,0);
                put(3,4,0);
                
                put(4,0,0);
                put(4,1,0);
                put(4,2,0);
                put(4,3,0);
                put(4,4,0);
                
            }
        };
        
        Imgproc.filter2D(source, dest,-1,kernel);
        String path=fpath+"lpf.jpg";
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
       
        return img;
    }
    
    
    public BufferedImage equalizehist() throws IOException//convert et ekrana bastır 
    {
       
       
            System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
            Mat sourceimg = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            Mat destination = new Mat(sourceimg.rows(),sourceimg.cols(),sourceimg.type());
            Imgproc.equalizeHist(sourceimg, destination);
            Imgproc.cvtColor(destination, destination, Imgproc.COLOR_GRAY2BGR);
            String path=fpath+"equalizehist.jpg";
            
            Imgcodecs.imwrite(path, destination);
            File out = new File(path);
            BufferedImage imge = ImageIO.read(out);
            return imge;
        
    }      
    
    public BufferedImage HoughTransform() throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        String path=fpath+"hough.jpg";
        Mat sourceimg=Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat Canny = new Mat();
        Imgproc.Canny(sourceimg, Canny, 0, 255, 3, true);
        Mat cannyColor = new Mat();
        Imgproc.cvtColor(Canny, cannyColor, Imgproc.COLOR_GRAY2BGR);
        
        Mat lines = new Mat();
        Imgproc.HoughLines(Canny, lines, 1, Math.PI/4, 100);

      System.out.println(lines.rows());
      System.out.println(lines.cols());

      // Drawing lines on the image
      double[] data;
      double rho, theta;
      Point pt1 = new Point();
      Point pt2 = new Point();
      double a, b;
      double x0, y0;
      
      for (int i = 0; i < lines.rows(); i++) {
         data = lines.get(i, 0);
         rho = data[0];
         theta = data[1];
         
         a = Math.cos(theta);
         b = Math.sin(theta);
         x0 = a*rho;
         y0 = b*rho;
         
         pt1.x = Math.round(x0 + 1000*(-b));
         pt1.y = Math.round(y0 + 1000*(a));
         pt2.x = Math.round(x0 - 1000*(-b));
         pt2.y = Math.round(y0 - 1000 *(a));
         Imgproc.line(cannyColor, pt1, pt2, new Scalar(0, 0, 255), 1);
      }
      // Writing the image
      Imgcodecs.imwrite(path, cannyColor);
          
      System.out.println("Image Processed");
      File out = new File(path);
      BufferedImage img = ImageIO.read(out);
      return img;
    }
    
    public BufferedImage ıncrementbrıghtness(int alpha,int beta) throws IOException
    {

            String path=fpath+"brigtness.jpg";
            System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
            Mat _source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
            Mat _dest = new Mat(_source.rows(),_source.cols(),_source.type());
            _source.convertTo(_dest, -1, alpha, beta);//her pixeli alpha ile çarp beta ile topla parlaklığı artır
            Imgcodecs.imwrite(path, _dest);
            File out = new File(path);
            BufferedImage img = ImageIO.read(out);
        
        return img;
    }
    
    public BufferedImage watermark(double alpha,double beta,double gamma) throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat _source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat _dest = new Mat(_source.rows(),_source.cols(),_source.type());
        Imgproc.GaussianBlur(_source, _dest, new Size(0,0), 10);
        Core.addWeighted(_source, alpha, _dest, beta, gamma, _dest);//alpha beta ile parlaklık açma kapama contrast gibi
        String path=fpath+"watermark.jpg";
        Imgcodecs.imwrite(path, _dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
                   
       
        return img;
    }
   
    
    
    public BufferedImage Compression(float quality) throws IOException
    {
        File input = new File(filepath);
        BufferedImage img = ImageIO.read(input);
        String path=fpath+"compress.jpg";
        File out = new File(path);
        OutputStream os = new FileOutputStream(out);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");//yazıcı sınıfından tureyen nesnenin kullanacagı formatı ıterator yardımı ile nesnenin değişkenini setliyor
        ImageWriter wt =(ImageWriter)writers.next();
        
        ImageOutputStream ios = ImageIO.createImageOutputStream(os); ByteArrayOutputStream ot = new ByteArrayOutputStream();ImageInputStream iis;
        wt.setOutput(ios);
        ImageWriteParam par = wt.getDefaultWriteParam();
        par.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        par.setCompressionQuality(quality);
        wt.write(null,new IIOImage(img, null, null),par);
        os.close();
        ios.close();
        wt.dispose();
        File output = new File(path);
        BufferedImage procimg=ImageIO.read(output);
        return procimg;
                
    }
    public BufferedImage Border(int top,int bottom,int left,int right) throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat _sourceimg=Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat _dest = new Mat(_sourceimg.rows(),_sourceimg.cols(),_sourceimg.type());
        if(top>_sourceimg.rows()||bottom>_sourceimg.rows())
        {
            JOptionPane.showMessageDialog(null, "overflow pixels");
            System.exit(1);
        }
        else if(left>_sourceimg.cols()||right>_sourceimg.cols())
        {
             JOptionPane.showMessageDialog(null, "overflow pixels");
            System.exit(1);
        }
        //_dest=_sourceimg;
        Core.copyMakeBorder(_sourceimg, _dest, top, bottom, left, right, Core.BORDER_CONSTANT);//bizde constant yap 
        String path=fpath+"border.jpg";
        Imgcodecs.imwrite(path, _dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
        
       
    }
    public BufferedImage imagepyramid(int scalar) throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat dest = new Mat(source.rows()/scalar,source.cols()/scalar,source.type());
        Imgproc.pyrDown(source, dest,new Size(source.rows()/scalar,source.cols()/scalar));
        String path=fpath+"smallimage.jpg";
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
        
    }
    public BufferedImage threshold(int T) throws IOException
    {
        int maxval=255;
        
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Imgproc.threshold(source, dest, T, maxval, Imgproc.THRESH_BINARY);
        String path=fpath+"threshold.jpg";
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        BufferedImage img =ImageIO.read(out);
        return img;
        
    }
    public BufferedImage GaussianBlur(int x,int y,int sapma) throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat _source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat _dest = new Mat(_source.rows(),_source.cols(),_source.type());
        Imgproc.GaussianBlur(_source, _dest, new Size(x,y), sapma);
        
        String path=fpath+"watermark.jpg";
        Imgcodecs.imwrite(path, _dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
    }
    public BufferedImage LaplaceTransform() throws IOException
    {
        int kernelSize=3;
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);//color yaparsak renkli türev alır
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize,CvType.CV_32F)
        {
            {
                put(0,0,0);//laplacian negative katsayıları toplamı 1 ediyo orta eleman 5 diğerleri -1 köseler // orjinal değerler 0,1,0,1,-4,1,0,1,0
                put(0,1,1);
                put(0,2,0);
                
                put(1,0,1);
                put(1,1,-4);//anchor 5 olur toplam 1 olur katsayılar toplamı 1 ise daha temiz bi goruntu renklide gridede 0 ise gride edge li goruntu
                put(1,2,1);//1.satır 2.sutuna 1 elemanını koy kernel = maske anchor= capa=merkez eleman i+1 j+1
                
                put(2,0,0);
                put(2,1,1);
                put(2,2,0);
                
                
            }
        };
        Imgproc.filter2D(source, dest,-1,kernel);
        String path=fpath+"convolute.jpg";
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
    }
    public BufferedImage MeanFilter() throws Exception
    {
        int kernelSize=3;
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);//color yaparsak renkli türev alır
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize,CvType.CV_32F)
        {
            {
                for(int i=0;i<3;i++)
                {
                    for(int j=0;j<3;j++)
                    {
                        put(i,j,0.11);
                    }
                }
               
            }
        };
        Imgproc.filter2D(source, dest,-1,kernel);
        String path=fpath+"meanfilter.jpg";
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
        
    }
    public BufferedImage Kirschoff() throws IOException
    {
        int kernelSize=3;
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize,CvType.CV_32F)
        {
            {
                for(int i=0;i<kernelSize;i++)
                {
                    put(0,i,-3);
                }
                
                put(1,0,-3);
                put(1,1,0);
                put(1,2,-3);
                
                put(2,0,5);
                put(2,1,5);
                put(2,2,5);
            }
        };
        Imgproc.filter2D(source, dest, -1, kernel);
        String path=fpath+"kirschoffmask.jpg";
        Imgcodecs.imwrite(path, dest);
        
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
        
    }
    public BufferedImage KirschoffWest() throws IOException
    {
        int kernelSize=3;
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat dest = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize,CvType.CV_32F)
        {
            {
                put(0,0,5);
                put(0,1,5);
                put(0,2,-3);
                
                put(1,0,5);
                put(1,1,0);
                put(1,2,-3);
                
                for(int i=0;i<kernelSize;i++)
                {
                    put(2,i,-3);
                }
            }
        };
        Imgproc.filter2D(source, dest, -1, kernel);
        String path = fpath+"kirschoffwestmask.jpg";
        Imgcodecs.imwrite(path, dest);
        
        File out = new File(path);
        BufferedImage img =ImageIO.read(out);
        
        return img;
    }
    public BufferedImage Zoom(int Xk) throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat dest = new Mat(source.rows()*Xk,source.cols()*Xk,source.type());
        Imgproc.resize(source, dest, dest.size(), Xk, Xk, Imgproc.INTER_CUBIC);
        String path = fpath+"Zoomxk.jpg";
       
        Imgcodecs.imwrite(path, dest);
        File out = new File(path);
        
        BufferedImage img =ImageIO.read(out);
        
        return img;
    }
    public BufferedImage DrawRectangle(int x,int y,int width,int heigth) throws IOException
    {
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source=Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Imgproc.rectangle(source, new Point(width,heigth), new Point(x,y), new Scalar(0,255,0));
        String path=fpath+"drawrectangle.jpg";
        Imgcodecs.imwrite(path, source);
        File out = new File(path);
        BufferedImage img = ImageIO.read(out);
        return img;
    }
    public BufferedImage Screenshots() throws IOException,InterruptedException
    {
        String path=fpath+"screenshot.jpg";
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat screen = new Mat();
        VideoCapture vc = new VideoCapture();
        vc.open(0);
        
        if(vc.isOpened())
        {
            vc.read(screen);
            vc.release();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Camera is Not Found");
        }
        
        Imgcodecs.imwrite(path, screen);
        File out = new File(path);
        BufferedImage img=ImageIO.read(out);
        return img;
    }
    
    public BufferedImage HaarCascadeClassifier() throws IOException
    {
        String haarpath="C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_frontalface_default.xml";
        String haarplatepath="C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_licence_plate_rus_16stages.xml";
        String haarbodypath="C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_fullbody.xml";
        String haarhasanpath="C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_myhaar.xml";
        
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        CascadeClassifier css = new CascadeClassifier(haarpath);
        CascadeClassifier plate = new CascadeClassifier(haarplatepath);
        CascadeClassifier body = new CascadeClassifier(haarbodypath);
        CascadeClassifier hasan = new CascadeClassifier(haarhasanpath);
        Mat source = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        MatOfRect mor = new MatOfRect();
        MatOfRect plt = new MatOfRect();
        MatOfRect bdy = new MatOfRect();
        MatOfRect has = new MatOfRect();
        int i=0,j=0;
        Rect crop =null;
        css.detectMultiScale(source, mor);
        plate.detectMultiScale(source, plt);
        body.detectMultiScale(source, bdy);
        hasan.detectMultiScale(source, has);
        for(Rect rc :mor.toArray())
        {
            i++;
            crop = new Rect(rc.x, rc.y, rc.width, rc.height);
            Mat face = new Mat(source,crop);
            String path=facepath+"face"+i+".jpg";
            Imgcodecs.imwrite(path, face);
            Imgproc.rectangle(source, new Point(rc.x,rc.y), new Point(rc.x+rc.width,rc.y+rc.height), new Scalar(0,0,255), 2);
        }
        for(Rect rc :plt.toArray())
        {
            j++;
            crop=new Rect(rc.x,rc.y,rc.width,rc.height);
            Mat plaka = new Mat(source,crop);
            String ppath=facepath+"plaka"+j+".jpg";
            Imgcodecs.imwrite(ppath, plaka);
            Imgproc.rectangle(source, new Point(rc.x,rc.y), new Point(rc.x+rc.width,rc.y+rc.height), new Scalar(255,0,0),2);
            
        }
        for(Rect r:bdy.toArray())
        {
            Imgproc.rectangle(source, new Point(r.x,r.y), new Point(r.x+r.width,r.y+r.height), new Scalar(127,127,255),2);
            Imgproc.putText(source, "Insan",new Point(r.x,r.y-5), 2, 2, new Scalar(127,127,255));
        }
        for(Rect rt:has.toArray())
        {
            Imgproc.rectangle(source, new Point(rt.x,rt.y), new Point(rt.x+rt.width,rt.y+rt.height), new Scalar(56,255,255),3);
            Imgproc.putText(source, "HASAN",new Point(rt.x,rt.y-5), 2, 2, new Scalar(56,255,255));
        }
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", source, mob);
        byte [] array = mob.toArray();
        InputStream is = new ByteArrayInputStream(array);
        BufferedImage imge = ImageIO.read(is);
        
        return imge;
    }
    public void Camera()
    { 
              
       Camera cm = new Camera();
       cm.setVisible(true);
       
       System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
       cam = new VideoCapture(0);
       double camwidth =cam.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
       double camheigth=cam.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
       cm.setSize((int)camwidth, (int)camheigth);
       
       Thread rec = new Thread()
       {
           public void run()
           {
               try{
               while(running)
               {
                   Mat frame1 = new Mat();
                   MatOfByte mob = new MatOfByte();
                   if(cam.isOpened())
                   {
                       cam.read(frame1);
                       
                       
                   }
                   Imgcodecs.imencode(".jpg", frame1, mob);
                   byte [] array=mob.toArray();
                   System.out.println(array.length);
                   InputStream  is=new ByteArrayInputStream(array);
                   
                   //is.read(array2,0,array2.length); //akışı değiştirmek için miş saçmalık
                   
                   
                   BufferedImage frame=ImageIO.read(is);Graphics g=cm.getGraphics();
                   g.drawImage(frame,0, 0, null);
               }
               cam.release();
               }catch(Exception e){e.printStackTrace();}
           }
           
       };
       rec.start();       
    }
    public void CameraHaarCascadeClassification()
    {
        Camera camera = new Camera();
        camera.setVisible(true);
        System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        String haarpath="C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_frontalface_default.xml";
        //String haareyepath="C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\etc\\haarcascades\\haarcascade_eye.xml";
        String haarfullbodypath="C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_fullbody.xml";
        //String haarupperbodypath="C:\\Users\\asus\\Desktophaarcascades\\haarcascade_upperbody.xml";
        String haarprofilefacepath= "C:\\Users\\asus\\Desktop\\haarcascades\\haarcascade_profileface.xml";
        CascadeClassifier face = new CascadeClassifier(haarpath);
        //CascadeClassifier eyes = new CascadeClassifier(haareyepath);
        CascadeClassifier fullbody = new CascadeClassifier(haarfullbodypath);
        //CascadeClassifier upperbody = new CascadeClassifier(haarupperbodypath);
        CascadeClassifier profile = new CascadeClassifier(haarprofilefacepath);
        cam = new VideoCapture(0);
        double camwidth =cam.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
        double camheigth=cam.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
        camera.setSize((int)camwidth, (int)camheigth);
        
        Thread rec = new Thread()
        {
            public void run()
            {
                try
                {
                    while(running)
                    {
                        Mat frame = new Mat();
                        if(cam.isOpened())
                        {
                            cam.read(frame);
                        }  
                            MatOfRect faces = new MatOfRect();
                            //MatOfRect eye = new MatOfRect();
                            //MatOfRect upbody = new MatOfRect();
                            MatOfRect body = new MatOfRect();
                            MatOfRect prof = new MatOfRect();
                            
                            face.detectMultiScale(frame, faces);
                            fullbody.detectMultiScale(frame, body);
                           // upperbody.detectMultiScale(frame, upbody);
                            profile.detectMultiScale(frame, prof);
                            
                            //eyes.detectMultiScale(frame, eye);
                            
                            for(Rect rect:faces.toArray())
                            {
                                Imgproc.rectangle(frame, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,0,0),2);
                                Imgproc.putText(frame, "Face", new Point(rect.x,rect.y-5), 2, 2, new Scalar(255,0,0));
                                
                            }
                            /*for(Rect rect:eye.toArray())
                            {
                                Imgproc.rectangle(frame, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0,255,0),2);
                                Imgproc.putText(frame, "Eye", new Point(rect.x,rect.y-5), 2, 2, new Scalar(0,255,0));
                            }*/
                            for(Rect rc:body.toArray())
                            {
                                Imgproc.rectangle(frame, new Point(rc.x,rc.y), new Point(rc.x+rc.width,rc.y+rc.height), new Scalar(0,0,255),3);
                                Imgproc.putText(frame, "Body", new Point(rc.x,rc.y-5), 1, 2, new Scalar(0,0,255));
                            }
                            /*for(Rect rect:upbody.toArray())
                            {
                                Imgproc.rectangle(frame, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,255,0),2);
                                Imgproc.putText(frame, "UpBody", new Point(rect.x,rect.y-5), 1, 2, new Scalar(255,255,0));
                                
                            }*/
                            for(Rect rect:prof.toArray())
                            {
                                Imgproc.rectangle(frame, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(128,255,67),2);
                                Imgproc.putText(frame, "Profile", new Point(rect.x,rect.y-5), 1, 2, new Scalar(128,255,67));
                                
                            }
                            
                            MatOfByte mob = new MatOfByte();
                            Imgcodecs.imencode(".jpg", frame, mob);
                            byte[]array = mob.toArray();
                            InputStream is = new ByteArrayInputStream(array);
                            BufferedImage imge =ImageIO.read(is);
                            Graphics g = camera.getGraphics();
                            g.drawImage(imge, 0, 0, null);
                    }
                    cam.release();
                }catch(Exception e){e.printStackTrace();}
            }
            
        };
        rec.start();
        
    }
    public ArrayList<Dnnobject> getobjectinframe()
    {
       
       System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
       net=Dnn.readNetFromCaffe(proto, model);
       Mat source = Imgcodecs.imread(filepath);
       
       int inwidth=300;
       int inheight=300;
       double scalefactor=0.007843;
       double meanvalue=127.5;
       double thresholddnn=0.2;
       int cols=source.cols();
       int rows=source.rows();
       Mat blob=null;
       Mat detections=null;
       ArrayList<Dnnobject> objelist = new ArrayList<Dnnobject>();
       try{ 
       blob=Dnn.blobFromImage(source, scalefactor, new Size(inwidth,inheight), new Scalar(meanvalue,meanvalue,meanvalue),true,false);
       net.setInput(blob);
       detections=net.forward();
       //System.out.println(detections.total());
       detections=detections.reshape(1, (int)detections.total()/7);
      System.out.println(detections.total()+" "+detections.rows());//7 kanallı bunu 1 kanala indirip 7 ye bölüyoruz gelen matrisi
      /* for(int i=0;i<detections.rows();i++)
       {
           for(int j=0;j<7;j++)
           {
               System.out.println((int)detections.get(i, j)[0]);
           }
       }*/
       
       for(int i=0;i<detections.rows();i++)
       {
           double confidence =detections.get(i,2)[0];
           if(confidence>thresholddnn){
              int classid = (int)detections.get(i, 1)[0];
           
           
           double startx =Math.abs(detections.get(i, 3)[0]*cols);//0,6 arası değerler
           double starty=Math.abs(detections.get(i, 4)[0]*rows);
           double finishx=Math.abs(detections.get(i, 5)[0]*cols);
           double finishy=Math.abs(detections.get(i, 6)[0]*rows);
           //System.out.println(startx+" "+starty+" "+finishx+" "+" "+finishy);
           
           Point startpos = new Point(startx,starty);
           Point finishpos = new Point(finishx,finishy);
           
           Dnnobject dnnobjesi = new Dnnobject(classid,classNames[classid].toString(),startpos, finishpos);
           objelist.add(dnnobjesi);
           }
          
       }
      
       }catch(Exception e){e.printStackTrace();}
      return objelist;
    }
    public BufferedImage DeepNeuralNetwork()
    {
        
        ArrayList<Dnnobject> a =getobjectinframe();
       System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        Mat source = Imgcodecs.imread(filepath);
        BufferedImage imge=null;
        Random r = new Random();
        Random g = new Random();
        Random b = new Random();
        for(int i=0;i<a.size();i++)
        {
            int red=r.nextInt(255);
            int green=g.nextInt(255);
            int blue=b.nextInt(255);
            Imgproc.rectangle(source, a.get(i).Startpos, a.get(i).finishpos, new Scalar(red,green,blue),2);
            Imgproc.putText(source, a.get(i).objename, new Point(a.get(i).Startpos.x,a.get(i).Startpos.y-5), 2, 2, new Scalar(red,green,blue));
            Imgcodecs.imwrite(fpath+"dnn.jpg", source);
        }
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", source, mob);
        byte[]array = mob.toArray();
        try{
            InputStream is = new ByteArrayInputStream(array);
            imge = ImageIO.read(is);
        }catch(Exception e){e.printStackTrace();}
        return imge;
    }
    public void CameraDeepNeuralNetwork()
    {
       System.load("C:\\Users\\asus\\Desktop\\opencv_modules\\opencv\\build\\java\\x64\\opencv_java343.dll");
        net=Dnn.readNetFromCaffe(proto,model);
        Camera cm = new Camera();
        cm.setVisible(true);
        cam = new VideoCapture(0);
        cam.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1024);
        cam.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 768);
        double camwidth =cam.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
        double camheigth=cam.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
        cm.setSize((int)camwidth, (int)camheigth);//her ağın giriş boyutları ve çıkış kanal sayısı farklı o yuzden hepsine ayrı yazılacak detections matris elemanları
        int inwidth=300;
       int inheight=300;
       double scalefactor=0.007843;
       double meanvalue=127.5;
       double thresholddnn=0.2;
       
       ArrayList<Dnnobject> dnnobje = new ArrayList<Dnnobject>();
       
        Thread rec = new Thread()
        {
            public void run()
            {
                try
                {
                    Mat frame = new Mat();
                    while(running)
                    {
                        if(cam.isOpened())
                        {
                            cam.read(frame);
                        }
                        int cols=frame.cols();
                        int rows=frame.rows();
                        
                        Mat blob=Dnn.blobFromImage(frame, scalefactor, new Size(inwidth,inheight), new Scalar(meanvalue,meanvalue,meanvalue), true, false);
                        net.setInput(blob);
                        Mat detections =net.forward();
                        detections=detections.reshape(1, (int)detections.total()/7);
                       
                        for(int i=0;i<detections.rows();i++)
                        {
                             
                            double confidence =detections.get(i,2)[0];
                             if(confidence>thresholddnn){
                                int classid = (int)detections.get(i, 1)[0];
                                System.out.println(classid);
                                double startx =Math.abs(detections.get(i, 3)[0]*cols);//0,6 arası değerler
                                double starty=Math.abs(detections.get(i, 4)[0]*rows);
                                double finishx=Math.abs(detections.get(i, 5)[0]*cols);
                                double finishy=Math.abs(detections.get(i, 6)[0]*rows);
           
           
                                Point startpos = new Point(startx,starty);
                                Point finishpos = new Point(finishx,finishy);
           
                                Dnnobject dnnobjesi = new Dnnobject(classid,classNames[classid].toString(),startpos, finishpos);
                                dnnobje.add(dnnobjesi);
                                }
                        }
                        
                        Random r = new Random();
                        Random g = new Random();
                        Random b = new Random();
                        for(int i=0;i<dnnobje.size();i++)
                        {
                            int red=r.nextInt(255);
                            int green=g.nextInt(255);
                            int blue=b.nextInt(255);
                            Imgproc.rectangle(frame, dnnobje.get(i).Startpos, dnnobje.get(i).finishpos, new Scalar(red,green,blue),1);
                            Imgproc.putText(frame, dnnobje.get(i).objename, new Point(dnnobje.get(i).Startpos.x,dnnobje.get(i).Startpos.y-5), 1, 2, new Scalar(red,green,blue));
                            dnnobje.remove(i);
                        }
                        MatOfByte mob = new MatOfByte();
                        Imgcodecs.imencode(".jpg", frame, mob);
                        byte[]array = mob.toArray();
                        InputStream is = new ByteArrayInputStream(array);
                        BufferedImage imge = ImageIO.read(is);
                        Graphics gr = cm.getGraphics();
                        gr.drawImage(imge, 0,0, null);
                        
                    }
                   
                  cam.release();
                }catch(Exception e){e.printStackTrace();}
            }
            
            
        };
        rec.start();
    }
    
}   
    
    İmageManipulation mnp = new İmageManipulation();
    public String filepath;
    BufferedImage img=null;
    public MyForm1() {
        initComponents();
       
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem31 = new javax.swing.JMenuItem();
        _sourceimg = new javax.swing.JLabel();
        _destimg = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem39 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenuItem36 = new javax.swing.JMenuItem();
        jMenuItem37 = new javax.swing.JMenuItem();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem40 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();

        jMenuItem31.setText("jMenuItem31");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Görüntü İşleme");

        _sourceimg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                _sourceimgMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                _sourceimgMouseReleased(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("GrayScale");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem4.setText("HistogramDraw");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem3.setText("BinaryImage");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem11.setText("WaterMark");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem13.setText("BorderImage");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem13);

        jMenuItem21.setText("DrawRectangle");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem21);

        jMenuItem22.setText("Screenshot");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem22);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Morp");

        jMenuItem5.setText("Dilation");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Erosion");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem7.setText("Boundary");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText("Canny Edge");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText("HistEqualize");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuItem10.setText("Brightness");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuItem12.setText("Compression");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);

        jMenuItem14.setText("İmagePyramid");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem14);

        jMenuItem15.setText("Threshold");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem15);

        jMenuItem30.setText("InverseImage");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem30);

        jMenuItem39.setText("ImageDifferent");
        jMenuItem39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem39ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem39);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Filters");

        jMenuItem18.setText("KirschoffEast");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem18);

        jMenuItem17.setText("LaplaceTransform");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem17);

        jMenuItem16.setText("GaussianBlur");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem16);

        jMenuItem19.setText("KirschoffWest");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem19);

        jMenuItem20.setText("Zoom");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem20);

        jMenuItem23.setText("Getpixel");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem23);

        jMenuItem29.setText("MeanFilter");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem29);

        jMenuItem32.setText("Hough");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem32);

        jMenuItem33.setText("DFT");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem33);

        jMenuItem34.setText("DCT");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem34);

        jMenuItem35.setText("LogPixel");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem35);

        jMenuItem36.setText("Exhaust");
        jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem36ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem36);

        jMenuItem37.setText("HPF");
        jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem37ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem37);

        jMenuItem38.setText("LPF");
        jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem38ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem38);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Object Detection");

        jMenuItem24.setText("HaarCascade");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem24);

        jMenuItem27.setText("DnnCaffeModel");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem27);

        jMenuItem40.setText("DCTT");
        jMenuItem40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem40ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem40);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Camera");

        jMenuItem25.setText("Open");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem25);

        jMenuItem26.setText("HaarCascade");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem26);

        jMenuItem28.setText("DeepNeuralNetwork");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem28);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(_sourceimg)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 564, Short.MAX_VALUE)
                .addComponent(_destimg)
                .addGap(201, 201, 201))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(_sourceimg))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(_destimg)))
                .addContainerGap(339, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        JFileChooser filechoose = new JFileChooser();
        int retval=filechoose.showOpenDialog(filechoose);
        if(retval==JFileChooser.APPROVE_OPTION)
        {
            filepath=filechoose.getSelectedFile().getAbsolutePath();
            try
            {
                img=ImageIO.read(new File(filepath));
                _sourceimg.setSize(img.getWidth(), img.getHeight());
                _sourceimg.setIcon(new ImageIcon(img));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }
        else
        {
            JOptionPane.showMessageDialog(null, "User Click to Cancel");
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
            _destimg.setSize(mnp.GrayScale().getWidth(),mnp.GrayScale().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.GrayScale()));
            
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         int []dizi = new int[256];
         dizi=mnp.histogram();
        /*for(int i=0;i<256;i++)
        {
            dizi[i]=mnp.histogram()[i];
        }*/
        for(int i=0;i<256;i++)
        {
            dataset.setValue(dizi[i],"pixelsayisi",""+i);
        }
        JFreeChart chart = ChartFactory.createBarChart("histogram", "pixeldegeri", "pixelsayisi", dataset,PlotOrientation.VERTICAL,false,true,false);
        CategoryPlot p = chart.getCategoryPlot();
        p.setRangeGridlinePaint(Color.black);
        ChartFrame fr = new ChartFrame("histogram", chart);
        fr.setVisible(true);
        fr.setSize(300, 300);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
            _destimg.setSize(mnp.GrayScale().getWidth(),mnp.GrayScale().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.BinaryImage()));
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
            _destimg.setSize(mnp.Dilation().getWidth(),mnp.Dilation().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.Dilation()));
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        _destimg.setSize(mnp.Erosion().getWidth(),mnp.Erosion().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.Erosion()));
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
            _destimg.setSize(mnp.Boundary().getWidth(),mnp.Boundary().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.Boundary()));
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
            _destimg.setSize(mnp.Canny_Edge().getWidth(),mnp.Canny_Edge().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.Canny_Edge()));
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        try
        {
            _destimg.setSize(mnp.equalizehist().getWidth(), mnp.equalizehist().getHeight());
           _destimg.setIcon(new ImageIcon(mnp.equalizehist()));
        }catch(Exception e){}
        
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
           try
           {_destimg.setSize(mnp.ıncrementbrıghtness(3, 30).getWidth(),mnp.ıncrementbrıghtness(3,30).getHeight());
            _destimg.setIcon(new ImageIcon(mnp.ıncrementbrıghtness(1, 5)));
           }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
            try
            {_destimg.setSize(mnp.watermark(3, -1, 0).getWidth(),mnp.watermark(3, -1, 0).getHeight());
            _destimg.setIcon(new ImageIcon(mnp.watermark(3.4, -3, -3)));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
            try
            {
                 _destimg.setSize(mnp.Compression(0.05f).getWidth(),mnp.Compression(0.05f).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.Compression(0.05f)));
            }
            catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
            try
            {
                 _destimg.setSize(mnp.Border(20, 20, 20, 20).getWidth(),mnp.Border(20, 20, 20, 20).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.Border(20, 20, 20, 20)));
            }
            catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
            try
            {
                 _destimg.setSize(mnp.imagepyramid(2).getWidth(),mnp.imagepyramid(2).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.imagepyramid(2)));
            }
            catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
            try
            {
                 _destimg.setSize(mnp.threshold(mnp.thres).getWidth(),mnp.threshold(mnp.thres).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.threshold(mnp.thres)));
            }
            catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
            try
            {
                 _destimg.setSize(mnp.GaussianBlur(45, 45, 10).getWidth(),mnp.GaussianBlur(45, 45,10).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.GaussianBlur(45, 45, 10)));
            }
            catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
            try
            {
                _destimg.setSize(mnp.LaplaceTransform().getWidth(), mnp.LaplaceTransform().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.LaplaceTransform()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
             try
            {
                _destimg.setSize(mnp.Kirschoff().getWidth(), mnp.Kirschoff().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.Kirschoff()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
             try
            {
                _destimg.setSize(mnp.KirschoffWest().getWidth(), mnp.KirschoffWest().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.KirschoffWest()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
             try
            {
                _destimg.setSize(mnp.Zoom(2).getWidth(), mnp.Zoom(2).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.Zoom(2)));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
             try
            {
                
                _destimg.setSize(mnp.DrawRectangle(20,20,50,50).getWidth(), mnp.DrawRectangle(20,20,50,50).getHeight());
                _destimg.setIcon(new ImageIcon(mnp.DrawRectangle(100,100,200,200)));
               
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
             try
            {
                _destimg.setSize(mnp.Screenshots().getWidth(), mnp.Screenshots().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.Screenshots()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
       mnp.getpixel();
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void _sourceimgMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event__sourceimgMousePressed
            mnp.x1=evt.getX();
            mnp.y1=evt.getY();
    }//GEN-LAST:event__sourceimgMousePressed

    private void _sourceimgMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event__sourceimgMouseReleased
            mnp.y2=evt.getY();
            mnp.x2=evt.getX();
            mnp.crop_w=mnp.x2-mnp.x1;
            mnp.crop_h=mnp.y2-mnp.y1;
            Rect crop=null;
           try{ 
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            Mat dest = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
            Mat copydest = new Mat(dest.rows(),dest.cols(),dest.type());
            for(int row=0;row<dest.rows();row++)
            {
                for(int col=0;col<dest.cols();col++)
                {
                    copydest.put(row, col, dest.get(row,col));
                }
            }
            
            if(mnp.crop_w>0&&mnp.crop_h>0)
            {
                  crop = new Rect(mnp.x1,mnp.y1,mnp.crop_w,mnp.crop_h);//point li verme mallaşıyo
                  Imgproc.rectangle(copydest, new Point(mnp.x1,mnp.y1), new Point(mnp.x2,mnp.y2), new Scalar(76,255,0));//point 2 biteceği yermiş ben width sanıyorum :D
            }
            else if(mnp.crop_w<0&&mnp.crop_h<0)
            {
                  crop = new Rect(mnp.x2,mnp.y2,Math.abs(mnp.crop_w),Math.abs(mnp.crop_h));//point li verme mallaşıyo
                  Imgproc.rectangle(copydest, new Point(mnp.x2,mnp.y2), new Point(mnp.x1,mnp.y1), new Scalar(76,255,0));
            }
            else if(mnp.crop_h>0&&mnp.crop_w<0)
            {
                  crop = new Rect(mnp.x2,mnp.y1,Math.abs(mnp.crop_w),mnp.crop_h);//point li verme mallaşıyo
                  Imgproc.rectangle(copydest, new Point(mnp.x2,mnp.y1), new Point(mnp.x1,mnp.y2), new Scalar(76,255,0));
            }
            else if(mnp.crop_h<0&&mnp.crop_w>0)
            {
                  crop = new Rect(mnp.x1,mnp.y2,mnp.crop_w,Math.abs(mnp.crop_h));//point li verme mallaşıyo
                  Imgproc.rectangle(copydest, new Point(mnp.x1,mnp.y2), new Point(mnp.x2,mnp.y1), new Scalar(76,255,0));
            }
            
            Mat destin = new Mat(dest,crop);
            String path=mnp.fpath+"croppedimage.jpg";
            Imgcodecs.imwrite(path, destin);
            Mat zoom = new Mat(destin.rows()*2,destin.cols()*2,destin.type());
            Imgproc.resize(destin, zoom, zoom.size(), 2, 2, Imgproc.INTER_CUBIC);
                MatOfByte mob2 = new MatOfByte();
                Imgcodecs.imencode(".jpg", zoom, mob2);
                byte [] zoomarray =mob2.toArray();
                InputStream iss = new ByteArrayInputStream(zoomarray);
                BufferedImage img =ImageIO.read(iss);
                _destimg.setSize(img.getWidth(), img.getHeight());
                _destimg.setIcon(new ImageIcon(img));
                MatOfByte mob = new MatOfByte();
                Imgcodecs.imencode(".jpg", copydest, mob);
                byte [] array = mob.toArray();
                InputStream is = new ByteArrayInputStream(array);
                BufferedImage img2 = ImageIO.read(is);
                
                //Imgproc.resize(destin, img2, 2, 2, 2, Imgproc.INTER_CUBIC);
                _sourceimg.setSize(img2.getWidth(), img2.getHeight());
                _sourceimg.setIcon(new ImageIcon(img2));
           }catch(Exception e){e.printStackTrace();}
            //dest =Imgcodecs.imread(filepath, Imgcodecs.CV_LOAD_IMAGE_COLOR);
           
           System.out.println(mnp.x1+" "+mnp.y1+" "+mnp.x2+" "+mnp.y2+" "+Math.abs(mnp.crop_w)+" "+Math.abs(mnp.crop_h));
    }//GEN-LAST:event__sourceimgMouseReleased

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
       try
       {
           _destimg.setSize(mnp.HaarCascadeClassifier().getWidth(), mnp.HaarCascadeClassifier().getHeight());
           _destimg.setIcon(new ImageIcon(mnp.HaarCascadeClassifier()));
       }catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        mnp.running=true;
        mnp.Camera();
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        mnp.running=true;
        mnp.CameraHaarCascadeClassification();
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        mnp.DeepNeuralNetwork();
        _destimg.setSize(mnp.DeepNeuralNetwork().getWidth(), mnp.DeepNeuralNetwork().getHeight());
        _destimg.setIcon(new ImageIcon(mnp.DeepNeuralNetwork()));
        
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        mnp.running=true;
        mnp.CameraDeepNeuralNetwork();
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
         try
            {
                _destimg.setSize(mnp.MeanFilter().getWidth(), mnp.MeanFilter().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.MeanFilter()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
       _destimg.setSize(mnp.InverseImage().getWidth(),mnp.InverseImage().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.InverseImage()));
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        try
            {
                _destimg.setSize(mnp.HoughTransform().getWidth(), mnp.HoughTransform().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.HoughTransform()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
         _destimg.setSize(mnp.DFT().getWidth(),mnp.DFT().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.DFT()));
        
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
         _destimg.setSize(mnp.DCT().getWidth(),mnp.DCT().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.DCT()));
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        _destimg.setSize(mnp.LogPixel().getWidth(),mnp.LogPixel().getHeight());
            _destimg.setIcon(new ImageIcon(mnp.LogPixel()));
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    private void jMenuItem36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem36ActionPerformed
         _destimg.setSize(mnp.exhaustive_method_for_image_forgery(4,4).getWidth(),mnp.exhaustive_method_for_image_forgery(50,50).getHeight());
            _destimg.setIcon(new ImageIcon(mnp.exhaustive_method_for_image_forgery(50,40)));
    }//GEN-LAST:event_jMenuItem36ActionPerformed

    private void jMenuItem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem37ActionPerformed
     try
     {
          _destimg.setSize(mnp.HPF().getWidth(), mnp.HPF().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.HPF()));
     }catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem37ActionPerformed

    private void jMenuItem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem38ActionPerformed
        try
     {
          _destimg.setSize(mnp.LPF().getWidth(), mnp.LPF().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.LPF()));
     }catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem38ActionPerformed

    private void jMenuItem39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem39ActionPerformed
         try
            {
                _destimg.setSize(mnp.Different().getWidth(), mnp.Different().getHeight());
                _destimg.setIcon(new ImageIcon(mnp.Different()));
            }catch(Exception e){}
    }//GEN-LAST:event_jMenuItem39ActionPerformed

    private void jMenuItem40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem40ActionPerformed
         
    }//GEN-LAST:event_jMenuItem40ActionPerformed
   
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MyForm1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyForm1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyForm1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyForm1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyForm1().setVisible(true);
                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel _destimg;
    public javax.swing.JLabel _sourceimg;
    public javax.swing.JMenu jMenu1;
    public javax.swing.JMenu jMenu2;
    public javax.swing.JMenu jMenu3;
    public javax.swing.JMenu jMenu4;
    public javax.swing.JMenu jMenu5;
    public javax.swing.JMenuBar jMenuBar1;
    public javax.swing.JMenuItem jMenuItem1;
    public javax.swing.JMenuItem jMenuItem10;
    public javax.swing.JMenuItem jMenuItem11;
    public javax.swing.JMenuItem jMenuItem12;
    public javax.swing.JMenuItem jMenuItem13;
    public javax.swing.JMenuItem jMenuItem14;
    public javax.swing.JMenuItem jMenuItem15;
    public javax.swing.JMenuItem jMenuItem16;
    public javax.swing.JMenuItem jMenuItem17;
    public javax.swing.JMenuItem jMenuItem18;
    public javax.swing.JMenuItem jMenuItem19;
    public javax.swing.JMenuItem jMenuItem2;
    public javax.swing.JMenuItem jMenuItem20;
    public javax.swing.JMenuItem jMenuItem21;
    public javax.swing.JMenuItem jMenuItem22;
    public javax.swing.JMenuItem jMenuItem23;
    public javax.swing.JMenuItem jMenuItem24;
    public javax.swing.JMenuItem jMenuItem25;
    public javax.swing.JMenuItem jMenuItem26;
    public javax.swing.JMenuItem jMenuItem27;
    public javax.swing.JMenuItem jMenuItem28;
    public javax.swing.JMenuItem jMenuItem29;
    public javax.swing.JMenuItem jMenuItem3;
    public javax.swing.JMenuItem jMenuItem30;
    public javax.swing.JMenuItem jMenuItem31;
    public javax.swing.JMenuItem jMenuItem32;
    public javax.swing.JMenuItem jMenuItem33;
    public javax.swing.JMenuItem jMenuItem34;
    public javax.swing.JMenuItem jMenuItem35;
    public javax.swing.JMenuItem jMenuItem36;
    public javax.swing.JMenuItem jMenuItem37;
    public javax.swing.JMenuItem jMenuItem38;
    public javax.swing.JMenuItem jMenuItem39;
    public javax.swing.JMenuItem jMenuItem4;
    public javax.swing.JMenuItem jMenuItem40;
    public javax.swing.JMenuItem jMenuItem5;
    public javax.swing.JMenuItem jMenuItem6;
    public javax.swing.JMenuItem jMenuItem7;
    public javax.swing.JMenuItem jMenuItem8;
    public javax.swing.JMenuItem jMenuItem9;
    // End of variables declaration//GEN-END:variables
}
