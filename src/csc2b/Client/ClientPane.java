/**
 * 
 */
package csc2b.Client;


import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;


/**
 * @author SN MAHLOBO
 *
 */
public class ClientPane extends GridPane
{
	//Declaring private variables
	
	private int portNumber;
	private Socket clientSocket;
	
	//Stream variables
	private BufferedReader readText;
	private DataOutputStream writeBinary;
	
	//URLs 
	private String grayScaleURL = "/api/GrayScale";
	private String CannyURL = "/api/Canny";
	private String ORBBURL = "/api/ORB";
	private String RotateURL = "/api/Rotate";
	
	
	//MIne add 
	private Label lblOriginalImg;
	private Label lblEditedImg;
	
	//image file uploaded
	private File imgFile;
		
	//GUI attributes
	private Button btnUpload;
	private ImageView ImageBefore;
	private ImageView ImageAfter;
	private Button btnGrayScale;
	private Button btnRotate;
	private Button btnCanny;
	private Button btnExit;
	private Button btnORB;
	private Button btnSaveImage;
	private TextArea TXTarea;
	private Image processedImage;
	
	
	
	
	/**
	 * Constructor 
	 * @param portNumber
	 *                   port number the client connect on.
	 *                   
	 */
	public ClientPane(int portNumber)
	{
		this.portNumber = portNumber;
		
		//Setting up the GUI
		setupGUI();
		
		
		//uploading image to process
		btnUpload.setOnAction((event)->
		{
			
			//Allowing the client to choose specific image file
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File("./data/images"));
			imgFile = fileChooser.showOpenDialog(null);
			
			Image image = new Image(imgFile.toURI().toString());
			ImageBefore.setImage(image );
			add(lblOriginalImg,0,1);
			
		});
		
		
		btnORB.setOnAction((event)-> 
		{
			connect();
			sendImage(ORBBURL);
			add(lblEditedImg,1,1);
			
		});
		
		
		btnGrayScale.setOnAction((event)->
		{
			connect();
			sendImage(grayScaleURL);
			add(lblEditedImg,1,1);
			
		});
		
		
		btnCanny.setOnAction((event)->
		{
			connect();
			sendImage(CannyURL);
			add(lblEditedImg,1,1);
			

		});
		
		
		btnRotate.setOnAction((event)->
		{
			connect();
			sendImage(RotateURL);
			add(lblEditedImg,1,1);
			
		});
		
		//Saving the processed Image 
		btnSaveImage.setOnAction((event)->
		{
					
			if(processedImage!=null)
			{
				String imageName = JOptionPane.showInputDialog("Image name Please...");
				imageToWrite(processedImage,imageName);
			}	
			
		});
		
		
		
		btnExit.setOnAction((event)->
		{
			try
			{
				if(clientSocket != null)
				{
					TXTarea.clear();
					writeBinary.close();
					readText.close();
					clientSocket.close();
					TXTarea.appendText("HAVE A NICE DAY!!");	
			   }
				
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			
		});
		
		
	}
	
	
	
	/**
	 * Method for connecting to the Server
	 * 
	 */
	private void connect() 
	{
		try
		{
			
			clientSocket = new Socket("localhost",portNumber);
			TXTarea.appendText("The Client is connected to server on port:"+ clientSocket.getLocalPort() +"\r\n");
			
			System.out.print(clientSocket.getLocalPort());
			//Initializing Streams
			readText = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writeBinary = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			
		} catch (IOException ex) 
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Method for setting up the GUI
	 * 
	 */
	private void setupGUI() 
	{
		
		setVgap(10);
		setHgap(10);
		setAlignment(Pos.CENTER);
		
		
		Label lblProcess = new Label("Pre-Processing");
		Label lblExtract = new Label("Feature Extraction");
		Label lblSaveImage = new Label("Save edited Image");
		
		btnUpload = new Button("Upload Image");
		
		
		lblOriginalImg = new Label("Original Image");
		lblEditedImg = new Label("Edited Image");
		
		btnGrayScale = new Button("GrayScale");
		btnGrayScale.setPrefWidth(200);

		btnCanny = new Button("Canny");
		btnCanny.setPrefWidth(250);

		btnRotate = new Button("Rotate");
		btnRotate.setPrefWidth(200);
		
		btnORB = new Button("ORB");
		btnORB.setPrefWidth(250);
		
		btnSaveImage = new Button("Save image");
		
		btnExit = new Button("Exit");
		
		
		ImageBefore = new ImageView();
		ImageBefore.setFitHeight(350);
		ImageBefore.setFitWidth(350);
		
		//setting up the start up image
		Image Startimage = new Image("file:data/ii.jpg");
		ImageBefore.setImage(Startimage);
		
		ImageAfter = new ImageView();
		ImageAfter.setFitHeight(350);
		ImageAfter.setFitWidth(350);
		
		TXTarea = new TextArea();
		TXTarea.setPrefHeight(100);
		
		//placing buttons and text box 
		add(btnUpload,1,0);
		add(ImageBefore,0,2);
		add(ImageAfter,1,2);
		add(lblProcess,0,3);
		add(lblExtract,1,3);
		add(lblSaveImage,2,3);
		add(btnGrayScale,0,4);
		add(btnCanny,1,4);
		add(btnSaveImage,2,4);
		add(btnExit,2,5);
		add(btnORB,1,5);
		add(btnRotate,0,5);
		add(TXTarea,0,6,5,1);
	}
	
	
	/**
	 * Method for sending an image from the server
	 * @param URL
	 *            the path of the image sent.
	 *            
	 */
	private void sendImage(String URL) 
	{
		
		String encodedFile = null;
		try {

			//sending image to the server 
			FileInputStream fileIs = new FileInputStream(imgFile);
			byte[] bytes = new byte[(int)imgFile.length()];
			fileIs.read(bytes);
			encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
			byte[] bytesToSend = encodedFile.getBytes();
			
			// POST HHTP REQUEST 
			writeBinary.write(("POST " + URL +" HTTP/1.1\r\n").getBytes());
			writeBinary.flush();
			writeBinary.write(("Content-Type: " +"application/text\r\n").getBytes());
			writeBinary.flush();
			writeBinary.write(("Content-Length: " + encodedFile.length() +"\r\n").getBytes());
			writeBinary.flush();
			writeBinary.write(("\r\n").getBytes());
			writeBinary.flush();
			writeBinary.write(bytesToSend);
			writeBinary.flush();
			writeBinary.write(("\r\n").getBytes());
			writeBinary.flush();
			TXTarea.appendText("POST Request sent\r\n");
			
			//read text response
			String response ="";
			String line="";
			
			while(!(line = readText.readLine()).equals("")) 
			{
				response += line +"\n";
			}
			
			TXTarea.appendText(response+"\r\n");
			System.out.println(response);
			
			//receiving response
			String imageData = "";
			
			while((line = readText.readLine())!=null) 
			{
				imageData += line;
			}
			
			String base64String = imageData.substring(imageData.indexOf('\'')+1,imageData.lastIndexOf('}')-1);
			
			byte[] decodedString = Base64.getDecoder().decode(base64String);
			
		    processedImage = new Image(new ByteArrayInputStream(decodedString));
		    
			ImageAfter.setImage(processedImage);
				
			
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param image
	 * @param Fname
	 */
      private void imageToWrite(Image image,String Fname)
      {
            BufferedImage bImage = null;
            
          try 
          {
                //File initialImage = new File("data/"+img);
               // bImage = ImageIO.read(initialImage);
            bImage = SwingFXUtils.fromFXImage(image, null);

            ImageIO.write(bImage, "gif", new File("data/saved images/"+Fname+".gif"));
            ImageIO.write(bImage, "jpg", new File("data/saved images/"+Fname+".png"));
            ImageIO.write(bImage, "bmp", new File("data/saved images/"+Fname+".bmp"));

          } catch (IOException e) 
          {
              System.out.println("Exception occured :" + e.getMessage());
          }
            
          System.out.println("Images were written succesfully.");
          
       }
	
	
}
