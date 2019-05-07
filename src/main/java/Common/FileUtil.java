package Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;


/**
 * 文件关联的共通函数
 *
 */
public class FileUtil {

    // 日志
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static String[] ZIP_FILE_ENCODING_ARR = {  "GB2312", "MS932", "GBK", "UTF8" };

    /**
     * 删除文件夹里面的所有文件（包含子目录）
     *
     * @param path 文件夹路径(绝对路径)
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // 先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i]);
                // 再删除空文件夹
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath (绝对路径)
     */
    public static void delFolder(String folderPath) {
        // 删除完里面所有内容
        delAllFile(folderPath);
        String filePath = folderPath;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
        // 删除空文件夹
        myFilePath.delete();
    }

    /**
     * 复制单个文件
     *
     * @param oldFilePathAndName 源文件路径
     * @param newFilePathAndName 复制后路径
     * @return 文件大小
     * @throws IOException
     */
    public static int copyFile(String oldFilePathAndName, String newFilePathAndName) throws IOException {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldFilePathAndName);
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldFilePathAndName); //读入原文件
                fs = new FileOutputStream(newFilePathAndName);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }

            }
            return bytesum;
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (fs != null) {
                fs.close();
            }
        }
    }

    /**
     * 删除指定路径下的文件
     *
     * @param filePathAndName 文件路径
     */
    public static void delFile(String filePathAndName) {
        String filePath = filePathAndName;
        filePath = filePath.toString();
        File myDelFile = new File(filePath);
        myDelFile.delete();
    }

    /**
     * 判断文件是否是图像文件
     * @param filePathAndName 文件路径
     */
    public static boolean isImage(String filePathAndName) {
        boolean valid = false;
        try {
            Image image = ImageIO.read(new File(filePathAndName));
            if (image != null) {
                valid = true;
            }
        } catch (Exception ex) {
            valid = false;
        }
        return valid;
    }

    /**
     * 判断文件是否是图像文件或是否为指定图片类型
     * @param filePathAndName 文件路径
     * @param imageType 图片类型
     */
    public static boolean isAllowableImageFileType(String filePathAndName, String[] allowTypes) {
        boolean valid = false;
        try {
            File image = new File(filePathAndName);
            if (ImageIO.read(image) != null) {
                if (allowTypes.length != 0) {
                    ImageInputStream imageInputStream  = ImageIO.createImageInputStream(image);
                    Iterator<ImageReader> iter = ImageIO.getImageReaders(imageInputStream);
                    if (null != iter && iter.hasNext()) {
                        ImageReader reader = iter.next();
                        String format = reader.getFormatName();
                        if (Arrays.asList(allowTypes).contains(format)) {
                            valid = true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            valid = false;
        }
        return valid;
    }

    /**
     * 将文件流保存成文件
     * @param inputStream 文件流
     * @param filePathAndName 保存文件路径
     * @throws IOException
     */
    public static void saveFileFromStream(final InputStream inputStream, String filePathAndName) throws IOException {
        File fp = new File(filePathAndName);
        FileOutputStream fs = null;
        try {
            // 将上传文件流保存至临时文件夹内
            int byteread = 0;
            byte[] buffer = new byte[1024];
            fs = new FileOutputStream(fp);
            while ((byteread = inputStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 创建文件夹
     * @param filePath 文件路径
     */
    public static boolean createDir(String filePath) {
        File fp = new File(filePath);
        if (fp.exists() && fp.isDirectory()) {
            return true;
        } else {
            return fp.mkdirs();
        }
    }

    /**
     * 创建文件名
     * @param filePath 文件路径
     * @param tempSuffix 文件扩展名
     */
    public static String createFileName(String filePath, String tempSuffix) {
        // 临时文件名取得
        String fileName = "";

        for (;;) {
            fileName = UUID.randomUUID().toString() + tempSuffix;
            // 临时文件相对路径取得
            filePath = filePath + fileName;
            File file = new File(filePath);
            if (!file.exists()) {
                break;
            }
        }

        return fileName;
    }

   /* *//**
     * 解压ZIP文件
     * @param zipFileFullPath zip文件绝对路径
     * @param unzipPath 解压文件夹绝对路径
     * @return 解压文件路径
     * @throws IOException
     *//*
    public static List<Map<String, Object>> unzipFile(String zipFileFullPath, String unzipPath) throws IOException {
        File baseFile = new File(zipFileFullPath);
        File baseDir = new File(baseFile.getParent(),
                baseFile.getName().substring(0, baseFile.getName().lastIndexOf(".")));
        baseDir.mkdir();
        ZipFile zipFile = null;
        List<Map<String, Object>> unzipFiles = new ArrayList<Map<String, Object>>();
        try {

            for (String encoding : ZIP_FILE_ENCODING_ARR) {
                try {
                    zipFile = new ZipFile(zipFileFullPath, encoding);
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
            if (zipFile == null) {
                return unzipFiles;
            }

            Enumeration<ZipEntry> enumZip = zipFile.getEntries();

            while (enumZip.hasMoreElements()) {

                ZipEntry zipEntry = (ZipEntry) enumZip.nextElement();

                File unzipFile = new File(unzipPath);

                File outFile = new File(
                        unzipFile.getAbsolutePath() + System.getProperty("file.separator") + baseDir.getName(),
                        zipEntry.getName());

//                if (zipEntry.isDirectory()) {
//                    outFile.mkdir();
//                } else {
//                    BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
//
//                    if (!outFile.getParentFile().exists())
//                        outFile.getParentFile().mkdirs();
//                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
//
//                    byte[] buffer = new byte[1024];
//
//                    int readSize = 0;
//                    while ((readSize = in.read(buffer)) != -1) {
//                        out.write(buffer, 0, readSize);
//                    }
//                    out.close();
//                    in.close();
////                    unzipFiles.add(outFile.getAbsolutePath());
//                }


                if (zipEntry.isDirectory()) {
                    outFile.mkdir();
                } else {
                    if (outFile.getName().getBytes().length > 255) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("false", outFile.getAbsolutePath());
                        unzipFiles.add(map);
                    } else {
                        BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry));

                        if (!outFile.getParentFile().exists())
                            outFile.getParentFile().mkdirs();
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));

                        byte[] buffer = new byte[1024];

                        int readSize = 0;
                        while ((readSize = in.read(buffer)) != -1) {
                            out.write(buffer, 0, readSize);
                        }
                        out.close();
                        in.close();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("true", outFile.getAbsolutePath());
                        unzipFiles.add(map);
                    }

//                        unzipFiles.add(outFile.getAbsolutePath());
                }
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }
        return unzipFiles;
    }

    *//**
     * 获取图片文件实际类型
     * 若不是图片则返回null
     *
     * @param strBase64 Base64文件字符串
     * @return 图片类型
     *//*
    public static String getBase64ImageType(String strBase64) {
        InputStream inputStream = null;
        ImageInputStream iis = null;
        String result = null;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            inputStream = new ByteArrayInputStream(decoder.decode(strBase64));
            iis = ImageIO.createImageInputStream(inputStream);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            if (iterator.hasNext()) {
                ImageReader reader = iterator.next();
                result = reader.getFormatName();
            }
        } catch (IOException e) {
        } finally {
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    *//**
     * 验证Base64图片文件大小
     * @param strBase64 Base64文件流
     * @param maxHeight 图片最大高度
     * @param maxWidth 图片最大宽度
     * @return 图片验证结果
     *//*
    public static boolean checkBase64ImageSize(String strBase64, int maxHeight, int maxWidth) {
        InputStream inputStream = null;
        boolean valid = false;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            inputStream = new ByteArrayInputStream(decoder.decode(strBase64));
            BufferedImage sourceImg = ImageIO.read(inputStream);
            if (sourceImg.getHeight() <= maxHeight &&
                    sourceImg.getWidth() <= maxWidth) {
                valid = true;
            }
        } catch (IOException e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return valid;
    }

    *//**
     * 将Base64文件流保存成文件
     * @param Base64Stream Base64文件流
     * @param filePathAndName 保存文件路径
     * @throws IOException
     *//*
    public static void saveFileFromBase64Stream(final String logoBase64, String filePathAndName)
            throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        InputStream inputStream = new ByteArrayInputStream(decoder.decode(logoBase64));
        saveFileFromStream(inputStream, filePathAndName);
    }
*/
    /**
     * 将数据保存成csv文件
     *
     * @param data 源数据List(二维数组)
     * @param filePathAndName 保存文件路径
     * @return
     */
    public static void saveCsvFileFromData(ArrayList<ArrayList<String>> data, String filePathAndName)
            throws IOException {
        BufferedWriter buffer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePathAndName), "GB2312"));
        try {
            for (int i = 0; i < data.size(); i++) {
                ArrayList<String> row = data.get(i);
                for (int j = 0; j < row.size(); j++) {
                    if (j > 0) {
                        buffer.write(",");
                    }
                    buffer.write(row.get(j));
                }
                // win 换行符 \r\n LINUX \r
                // 如果用buffer.newLine(); LINUX发布时系统读取/r，再用win访问无法换行
                // 手动注入 \r\n
                buffer.write("\r\n");
            }
        } finally {
            buffer.flush();
            buffer.close();
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldFilePathAndName 源文件路径
     * @param newFilePathAndName 复制后路径
     * @return 文件大小
     * @throws IOException
     */
    public static InputStream getFileToStream(String filePath) throws IOException {
        InputStream inStream = new FileInputStream(filePath);
        return inStream;
    }

    /**
     * 获取指定父文件夹
     *
     * @param filePath 文件路径
     * @param i 父文件夹个数
     * @return 父文件夹路径
     */
    public static String getFatherPath(String filePath, int i) {
        File file = new File(filePath);
        for (int k = 0; k < i; k++) {
            file = file.getParentFile();
        }
        String fatherPath = file.getAbsolutePath() + File.separator;
        return fatherPath;
    }

    /**
     * 获取缩略图
     *
     * @param filePathAndName 原图绝对路径
     * @param thumbnailFilePathAndName 缩略图绝对路径
     *//*
    public static boolean getThumbnailImage(String filePathAndName, String thumbnailFilePathAndName) {
        if (PropertiesUtil.getValue("image.zip.flag").equals("1")) {
            // 需要压缩的场合
            File srcfile = new File(filePathAndName);
            // 检查原图是否存在
            if (!srcfile.exists()) {
                return false;
            }
            // 获取图片最大的长或宽
            int maxWidth = Integer.parseInt(PropertiesUtil.getValue("image.max.thumbnail.width"));
            InputStream is = null;
            BufferedImage src = null;
            int width = 0;
            int height = 0;
            try {
                // 获得文件输入流
                is = new FileInputStream(srcfile);
                // 从流里将图片写入缓冲图片区
                src = ImageIO.read(is);
                // 得到源图片宽
                width =src.getWidth(null);
                // 得到源图片高
                height =src.getHeight(null);
                //关闭输入流
                is.close();
                if (width <= maxWidth && height <= maxWidth) {
                    // 原照片在规定尺寸范围内的场合
                    copyFile(filePathAndName, thumbnailFilePathAndName);
                } else if (width > maxWidth && height <= maxWidth) {
                    // 原照片宽大于尺寸范围的场合
                    // 取得原照片缩放比例
                    double scale_w =getDot2Decimal(maxWidth, width);
                    // 取得压缩照片高度
                    height = (int) (height * scale_w);
                    // 取得压缩照片宽度
                    width = maxWidth;
                } else if (width <= maxWidth && height > maxWidth) {
                    // 原照片高大于尺寸范围的场合
                    // 取得原照片缩放比例
                    double scale_h =getDot2Decimal(maxWidth, height);
                    // 取得压缩照片宽度
                    width = (int) (width * scale_h);
                    // 取得压缩照片高度
                    height = maxWidth;
                } else {
                    // 原照片高和宽都大于尺寸范围的场合
                    if (width >= height) {
                        // 原照片宽大于高的场合
                        // 取得原照片缩放比例
                        double scale_w =getDot2Decimal(maxWidth, width);
                        // 取得压缩照片高度
                        height = (int) (height * scale_w);
                        // 取得压缩照片宽度
                        width = maxWidth;
                    } else {
                        // 取得原照片缩放比例
                        double scale_h =getDot2Decimal(maxWidth, height);
                        // 取得压缩照片宽度
                        width = (int) (width * scale_h);
                        // 取得压缩照片高度
                        height = maxWidth;
                    }
                }
                scaleImageWithParams(filePathAndName, thumbnailFilePathAndName, width, height);
            } catch (Exception ef) {
                StringBuffer strbuffer = new StringBuffer();
                StackTraceElement[] stackArray = ef.getStackTrace();
                for (int k = 0; k < stackArray.length; k++) {
                    StackTraceElement element = stackArray[k];
                    strbuffer.append(element.toString() + "\n");
                }
                logger.error(strbuffer.toString());
                return false;
            }
        } else {
            try {
                copyFile(filePathAndName, thumbnailFilePathAndName);
            } catch (Exception ef) {
                StringBuffer strbuffer = new StringBuffer();
                StackTraceElement[] stackArray = ef.getStackTrace();
                for (int k = 0; k < stackArray.length; k++) {
                    StackTraceElement element = stackArray[k];
                    strbuffer.append(element.toString() + "\n");
                }
                logger.error(strbuffer.toString());
                return false;
            }
        }

        return true;
    }

    *//***
     * 将图片缩放到指定的高度或者宽度
     * @param sourceImagePath 图片源地址
     * @param destinationPath 压缩完图片的地址
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     *//*
    public static boolean scaleImageWithParams(String sourceImagePath,
        String destinationPath, int width, int height) {

        try {
            File file = new File(sourceImagePath);
            BufferedImage bufferedImage = null;
            bufferedImage = ImageIO.read(file);
            ArrayList<Integer> paramsArrayList = getAutoWidthAndHeight(bufferedImage,width,height);
            width = paramsArrayList.get(0);
            height = paramsArrayList.get(1);

            Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = outputImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            ImageIO.write(
                    outputImage, file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase(),
                    new File(destinationPath));
        } catch (Exception e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
            return false;
        }

        return true;
    }

    *//***
     *
     * @param bufferedImage 要缩放的图片对象
     * @param width_scale 要缩放到的宽度
     * @param height_scale 要缩放到的高度
     * @return 一个集合，第一个元素为宽度，第二个元素为高度
     *//*
    private static ArrayList<Integer> getAutoWidthAndHeight(BufferedImage bufferedImage,int width_scale,int height_scale) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        double scale_w =getDot2Decimal(width_scale, width);
        double scale_h = getDot2Decimal(height_scale, height);
        if (scale_w < scale_h) {
            arrayList.add(parseDoubleToInt(scale_w * width));
            arrayList.add(parseDoubleToInt(scale_w * height));
        } else {
            arrayList.add(parseDoubleToInt(scale_h * width));
            arrayList.add(parseDoubleToInt(scale_h * height));
        }
        return arrayList;
    }
*/
    /**
     * 将double类型的数据转换为int，四舍五入原则
     *
     * @param sourceDouble
     * @return
     */
    private static int parseDoubleToInt(double sourceDouble) {
        int result = 0;
        result = (int) sourceDouble;
        return result;
    }


  /***
   * 返回两个数a/b的小数点后三位的表示
   * @param a
   * @param b
   * @return
   */
  public static double getDot2Decimal(int a,int b) {
      BigDecimal bigDecimal_1 = new BigDecimal(a);
      BigDecimal bigDecimal_2 = new BigDecimal(b);
      BigDecimal bigDecimal_result = bigDecimal_1.divide(bigDecimal_2,new MathContext(4));
      Double double1 = new Double(bigDecimal_result.toString());
      return double1;
  }

    /**
     * 判断文件大小
     *
     * @param filePathAndName 文件绝对路径
     * @param size 文件最大值
     */
    public static boolean checkFileSize(String filePathAndName, String maxSize) {
        File file = new File(filePathAndName);
        // 检查文件是否存在
        if (!file.exists()) {
            return false;
        }
        // 文件大小
        Long size = file.length() / 1024;
        Long maxSizeLong = Long.parseLong(maxSize);
        if (size > maxSizeLong) {
            return false;
        }
        return true;
    }

   /* *//**
     * 获取照片exif
     *
     * @param filePathAndName 文件绝对路径
     *//*
    public static Map<String,Object> getExif(String fileName){
        Map<String,Object> map = new HashMap<String,Object>();
        File file = new File(fileName);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            map = printExif(file,metadata);
        } catch (ImageProcessingException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        } catch (IOException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
        return map;
    }

    *//**
     * 获取照片旋转角度信息
     *
     * @param file 文件流
     * @param metadata 图片信息
     *//*
    private static Map<String,Object> printExif(File file,Metadata metadata){
        Map<String,Object> map = new HashMap<String,Object>();
        String tagName = null;
        String desc = null;
        for(Directory directory : metadata.getDirectories()){
            for(Tag tag : directory.getTags()){
                tagName = tag.getTagName();
                desc = tag.getDescription();
                if(tagName.equals("Orientation")){
                    map.put("Orientation", desc);
                }
            }
        }
        return map;
    }
*/
  /*  *//**
     * 获取照片方向
     *
     * @param map 照片旋转角度信息
     *//*
    public static int getAngle(Map<String,Object> map){
        int ro = 0;
        if (StrUtil.isNull(map.get("Orientation"))) {
            return ro;
        }
        String ori = map.get("Orientation").toString();
        if(ori.indexOf("90")>=0){
            ro=1;
        }else if(ori.indexOf("180")>=0){
            ro=2;
        }else if(ori.indexOf("270")>=0){
            ro=3;
        }
        return ro;
    }

    *//**
     * 旋转照片
     *
     * @param src 照片Buffer
     * @param width 照片宽度
     * @param height 照片高度
     * @param ro 照片方向
     *//*
    public static BufferedImage getBufferedImg(BufferedImage src,int width,int height,int ro){
        int angle = (int)(90*ro);
        int type = src.getColorModel().getTransparency();
        int wid = width;
        int hei = height;
        if(ro%2!=0){
            int temp = width;
            width = height;
            height = temp;
        }
        Rectangle re = new Rectangle(new Dimension(width, height));
        BufferedImage BfImg = null;
        BfImg = new BufferedImage(re.width, re.height, type);
        Graphics2D g2 = BfImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.rotate(Math.toRadians(angle),re.width/2,re.height/2);
        g2.drawImage(src,(re.width-wid)/2,(re.height-hei)/2,null);
        g2.dispose();
        return BfImg;
    }

    *//**
     * 照片转正
     *
     * @param filePathAndName 文件绝对路径
     *//*
    public static void rotateImage(String filePathAndName) {
        File img = new File(filePathAndName);
        int angle = getAngle(getExif(filePathAndName));
        if (angle == 0) {
            return;
        }

        InputStream is=null;
        BufferedImage src=null;
        try {
            is = new FileInputStream(img);
            src = ImageIO.read(is);
        } catch (FileNotFoundException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        } catch (IOException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
        //旋转
        BufferedImage bf = getBufferedImg(src, getWidth(img), getHeight(img), angle);
        try {
            ImageIO.write(bf, "jpg", new File(filePathAndName));
        } catch (IOException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
    }

    *//**
     * 获取照片高度
     *
     * @param file 文件流
     *//*
    public static int getHeight(File file){
        InputStream is = null;
        BufferedImage src = null;
        int height = -1;
        try {
            is = new FileInputStream(file);
            src = ImageIO.read(is);
            height = src.getHeight();
        } catch (FileNotFoundException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        } catch (IOException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
        return height;
    }

    *//**
     * 获取照片宽度
     *
     * @param file 文件流
     *//*
    public static int getWidth(File file){
        InputStream is = null;
        BufferedImage src = null;
        int width = -1;
        try {
            is = new FileInputStream(file);
            src = ImageIO.read(is);
            width = src.getWidth();
        } catch (FileNotFoundException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        } catch (IOException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
        return width;
    }

    *//**
     * 将图片转换成Base64编码
     * @param FilePathAndName 图片文件路径
     * @return
     *//*
    public static String getBase64ImageStr(String FilePathAndName){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(FilePathAndName);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedText = encoder.encodeToString(data);
        return encodedText;
    }

    *//**
     * 获取图片文件实际类型
     * 若不是图片则返回null
     *
     * @param strBase64 Base64文件字符串
     * @return 图片类型
     *//*
    public static void saveBase64ImageStrToImage(String strBase64, String FilePathAndName) {
        InputStream inputStream = null;
        Base64.Decoder decoder = Base64.getDecoder();
        inputStream = new ByteArrayInputStream(decoder.decode(strBase64));
        try {
            saveFileFromStream(inputStream, FilePathAndName);
        } catch (IOException e) {
            StringBuffer strbuffer = new StringBuffer();
            StackTraceElement[] stackArray = e.getStackTrace();
            for (int k = 0; k < stackArray.length; k++) {
                StackTraceElement element = stackArray[k];
                strbuffer.append(element.toString() + "\n");
            }
            logger.error(strbuffer.toString());
        }
    }

    *//**
     * 把url保存到本地
     * @return 图片类型
     *//*
    public static void saveToFile(String destUrl, String saveUrl) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        File file = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            // 查看文件夹是否已经创建
            // TODO
            file = new File(saveUrl).getParentFile();
            if (!file.exists()) {
                file.mkdirs();
            }

            fos = new FileOutputStream(saveUrl);
            while ((size = bis.read(buf)) != -1) {
                fos.write(buf, 0, size);
            }
            fos.flush();
        } catch (IOException e) {
            logger.error(destUrl+ "不是有效url");
        } catch (ClassCastException e) {
        } finally {
            try {
                fos.close();
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
            } catch (NullPointerException e) {
            }
        }
    }
*/
    //测试
    public static void main(String[] args) {
    }

}
