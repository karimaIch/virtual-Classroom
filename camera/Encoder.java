package camera;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class Encoder implements Runnable{
	String file;
	public Encoder(String file){this.file=file;}
	public void run() {

		File file = new File(this.file);//we are recording on the file's location
		IMediaWriter writer = ToolFactory.makeWriter(file.getName());
		Dimension size = WebcamResolution.QVGA.getSize();

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

		Webcam webcam = Webcam.getDefault();;
		webcam.setViewSize(size);
		webcam.open();

		long start = System.currentTimeMillis();
		int i=0;
try{
		while (!Thread.interrupted()){


			BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
			IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

			IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
			frame.setKeyFrame(i == 0);
			frame.setQuality(0);

			writer.encodeVideo(0, frame);

			
		}
}
catch(Exception e)
{
	writer.close();
}
			
		//System.out.println("Video recorded in file: " + file.getAbsolutePath());
	}
}
