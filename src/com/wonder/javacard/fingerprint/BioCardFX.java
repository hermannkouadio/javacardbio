/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wonder.javacard.fingerprint;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.suprema.BioMiniSDK;
import com.wonder.javacar.client.FingerManagerService;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author kherman
 */
public class BioCardFX extends Application {

    int nC = 0, capture = 0;
    boolean extractionStatus = false;
    
    public static WritableImage imageL;

    // Last Template
    int initFlag = 0;
    int nCaptureFlag = 0;

    //Special canvas
    private Canvas canvas;
    private GraphicsContext canvasGraphicsContext;

    // Special JFrame
    private JFrame imageFrame;
    private JPanel field_panel;
    private JButton btn_capture, btn_init, btn_verify, btn_enroll;
    private JTextField tf_name, tf_mobile, tf_email, tf_age;
    private JComboBox cb_blood;
    private JLabel lb_name, lb_mobile, lb_email, lb_age, lb_blood;

    private VBox leftBox, fingerPane, centerBox;
    //finger image panel
    private ImagePanel imgPanel;
    private JFrame fingerFrame;
    private ImageView fingerView;

    private FlowPane root;
    private Scene scene;

    private JPanel jContentPane = null;

    private static int nSecurityLevel = 0;
    private static int nDetectFake = 2;
    private static int nFastMode = 0;

    public static BioCardFX pMainInstance;
    public static BioMiniSDK p;
    public static Scanner currentScanner;
    public static ArrayList<Scanner> scannerList = new ArrayList<>();

    // Template List for verification
    private ArrayList<byte[]> templateList = new ArrayList<>();

    // User
    private User user;

    // File for storage
    private File storageFile;
    private FileOutputStream fout;

    public static final int MAX_TEMPLATE_SIZE = 1024;
    private static byte[][] byteTemplateArray = null;

    private static int[] intTemplateSizeArray = null;

    private static int nTemplateCnt = 0;

    private static String[] strTemplateArray = null;

    private static int nScannerNumber = 0;
    static long[] hMatcher = new long[1];

    @Override
    public void start(Stage primaryStage) {
        //GridPane root = new GridPane();

        primaryStage.setTitle("BioCard App!");
        primaryStage.setScene(this.scene);
        this.scene.getStylesheets().add(BioCardFX.class.getResource("main.css").toExternalForm());
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BioCardFX thisClass = new BioCardFX();
            pMainInstance = thisClass;
        });
        launch(args);
    }

    /*
        CUSTOM METHODS
     */
    private VBox getLeftVBox() {
        leftBox = new VBox();
        leftBox.setId("left-box");
        leftBox.setSpacing(20);
        leftBox.setPadding(new Insets(10));
        leftBox.setAlignment(Pos.TOP_LEFT);

        Button btnInit = new Button("Init");
        btnInit.setOnAction((ActionEvent e) -> {
            System.out.println("Init button");
            if (initFlag == 0) {
                initSDK();
            } else {
                MsgBox("Already init");
            }
        });

        Button btnSingleCapture = new Button("Single Capture");
        btnSingleCapture.setOnAction((ActionEvent e) -> {
            System.out.println("ReScann Finger");
            // Set first scanner as current scnner
            if (currentScanner != null && initFlag == 1) {
                //startCapturing(currentScanner);
                int testCallStartCapturing = testCallStartCapturing();
            } else {
                System.out.println("No Scanner Connected or Init first Scanner");
            }
        });
        //add btn to leftBox
        leftBox.getChildren().addAll(btnInit, btnSingleCapture);

        return leftBox;
    }

    private VBox getCenterVBox() {
        centerBox = new VBox();
        centerBox.setId("center-box");
        centerBox.setSpacing(10);
        centerBox.setPadding(new Insets(5));
        centerBox.setAlignment(Pos.CENTER_RIGHT);

        Label lbUser = new Label("User Name");
        Label lbMobile = new Label("Mobile");
        Label lbAge = new Label("Age");
        Label lbEmail = new Label("Email");
        Label lbBloodGroup = new Label("Blood Group");
        TextField tfUser = new TextField();
        TextField tfMobile = new TextField();
        TextField tfAge = new TextField();
        TextField tfEmail = new TextField();

        ComboBox<String> cbBloodGroup = new ComboBox<>();
        cbBloodGroup.getItems().addAll("O+", "AB+", "B+", "A+", "O-", "AB-", "B-", "A-");
        cbBloodGroup.setValue("O+");
        cbBloodGroup.setVisibleRowCount(4);
        cbBloodGroup.setOnAction((ActionEvent e) -> {
            if (cbBloodGroup.getValue() != null && cbBloodGroup.getValue().toString().isEmpty()) {
                cbBloodGroup.setValue(cbBloodGroup.getSelectionModel().getSelectedItem().toString());
            }
        });

        Button btnEnroll = new Button("Enroll");
        btnEnroll.setOnAction((ActionEvent e) -> {
            this.canvasGraphicsContext = this.canvas.getGraphicsContext2D();

            /*test draw image*/
            int[] Resolution = new int[1];
            int[] Height = new int[1];
            int[] Width = new int[1];
            long[] hScanner = new long[1];
            if (initFlag == 0) {
                initSDK();
            }
            // Check input value
            if (tfUser.getText().equals("") && tfMobile.getText().equals("")) {
                MsgBox("Name and Mobile required !");
            } else {
                String name = tfUser.getText();
                String mobile = tfMobile.getText();
                String email = tfEmail.getText();
                String age = tfAge.getText();
                String bloodGroup = cbBloodGroup.getSelectionModel().getSelectedItem().toString();
                user = new User(name, mobile, email, age, bloodGroup);

                //Start capture --> extract template --> save it as user data
                MsgBox("Put your finger on Scanner");
                testCallStartCapturing();
                user.setTemplate(extractTemplate(currentScanner));

                //Add template to general list
                templateList.add(extractTemplate(currentScanner));

                // Save all data in file
                if(extractionStatus) {
                	try {
                        fout = new FileOutputStream(storageFile.getPath(), true);
                        BufferedOutputStream bout = new BufferedOutputStream(fout);
                        String headerString = "------USER--" + User.getId() + "-----\n\n";
                        String footerString = "\n\n------END--FOR--USER--" + User.getId() + "-----\n\n\n";
                        bout.write(headerString.getBytes());
                        //Additional Informations
                        bout.write(user.toString().getBytes());
                        bout.write("\n\t".getBytes());
                        bout.write(user.decryptTemplate().getBytes());
                        bout.write(footerString.getBytes());
                        //Flush and Close buffer and file
                        bout.close();
                        fout.close();
                        
                        // Send Finger Print Template To Card
                        FingerManagerService.finger = user.getTemplate();
                        
                        MsgBox("Operation Successful !");
                        tfUser.setText("");
                        tfMobile.setText("");
                        tfEmail.setText("");
                        tfAge.setText("");
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(BioCardFX.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    } catch (IOException ex) {
                        Logger.getLogger(BioCardFX.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }else {
                	MsgBox("Retry Enrollement Operation, something went wrong.");
                }
            }
        });
        //add btn to centerBox
        centerBox.getChildren().addAll(lbUser, tfUser, lbAge, tfAge, lbMobile, tfMobile, lbEmail, tfEmail, lbBloodGroup, cbBloodGroup, btnEnroll);
        return centerBox;
    }

    private VBox getFPandImageBox() {
        fingerPane = new VBox();
        fingerPane.setId("fpi-box");
        fingerPane.setSpacing(20);
        fingerPane.setPadding(new Insets(10));
        fingerPane.setAlignment(Pos.TOP_LEFT);

        if (this.canvas == null) {
            this.canvas = new Canvas(200, 200);
        }
        this.canvasGraphicsContext = this.canvas.getGraphicsContext2D();
        //Canvas lCanvas = new Canvas(250, 250);
        //lCanvas.setId("mycanvas");
        Button bnt = new Button("Other Capture");

        bnt.setOnAction((ActionEvent e) -> {
            GraphicsContext lGc = this.canvas.getGraphicsContext2D();

            if (capture == 0) {
                double width = lGc.getCanvas().getWidth();
                double height = lGc.getCanvas().getHeight();

                Random random = new Random(System.currentTimeMillis());

                lGc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255),
                        random.nextInt(255), 0.9));
                lGc.translate(width / 2, height / 2);

                for (int i = 0; i < 60; i++) {
                    lGc.rotate(6.0);
                    lGc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255),
                            random.nextInt(255), 0.9));
                    lGc.fillOval(10, 60, 30, 30);

                    lGc.strokeOval(60, 60, 30, 30);
                    lGc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255),
                            random.nextInt(255), 0.9));
                    lGc.fillRoundRect(110, 60, 30, 30, 10, 10);
                    lGc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255),
                            random.nextInt(255), 0.9));
                    lGc.fillPolygon(
                            new double[]{105, 117, 159, 123, 133, 105, 77, 87, 51, 93},
                            new double[]{150, 186, 186, 204, 246, 222, 246, 204, 186, 186}, 10);
                }
                capture = 1;
            } else {
                lGc.clearRect(0, 0, lGc.getCanvas().getWidth(), lGc.getCanvas().getHeight());
                capture = 0;
            }

            //lGc.drawImage(getImageL(), getImageL().getWidth(), getImageL().getHeight());
            lGc.restore();
        });

        fingerPane.getChildren().addAll(this.canvas, bnt);
        return fingerPane;
    }

    public BioCardFX() {
        super();
        this.root = new FlowPane();
        this.root.setPadding(new Insets(10));
        this.scene = new Scene(this.root, 640, 480);
        p = new BioMiniSDK();
        fingerFrame = new JFrame("Finger Frame");
        storageFile = new File("C:\\Users\\kherman\\Desktop\\java\\biocardinfolist.txt");

        this.leftBox = getLeftVBox();
        this.centerBox = getCenterVBox();
        this.fingerPane = getFPandImageBox();
        this.root.getChildren().addAll(leftBox, centerBox, fingerPane);
    }

    public void scannerCallback(char[] szScannerID, int bSensorOn) {
        nC++;

        System.out.println(nC + "==========================================");  //
        //System.out.println("==>ScanProc calle scannerID:"+szScannerId);  //
        System.out.println("sensoron value is " + bSensorOn);
        System.out.println("void * pParam  value is " + bSensorOn);
        System.out.println(nC + "==========================================");  //

        //fv.UpdateScannerList(currentScanner.getDeviceHandle(), p);
    }

    public void captureCallback(int bFingerOn, byte[] pImage, int nWidth, int nHeight, int nResolution) {
        // ....
        //fv.drawCurrentFingerImage(currentScanner.getDeviceHandle(), p);
        pMainInstance.drawCurrentFingerImage();
    }

    public static void startCapturing(Scanner sc) {
        System.out.println("into startCapturing() method");
        System.out.println(sc.toString());
        int[] scannerGeneralOutPut = new int[1];
        int nRes;

        // Check if Scanner's connected 
        nRes = p.UFS_IsSensorOn(sc.getDeviceHandle(), scannerGeneralOutPut);
        if (p.UFS_OK == nRes) {
            System.out.println("Device " + sc.getDeviceIndex() + " connected");

            do {
                nRes = p.UFS_IsFingerOn(sc.getDeviceHandle(), scannerGeneralOutPut);
            } while (nRes != p.UFS_OK);

            if (p.UFS_OK == nRes) {
                System.out.println("Finger's on device\t" + nRes);
                //drawFingerViewer();
            } else {
                System.out.println("No finger on device\t" + nRes);
            }
        } else {
            System.out.println("Device " + sc.getDeviceIndex() + " not connected");
            return;
        }

        p.UFS_ClearCaptureImageBuffer(sc.getDeviceHandle());
        System.out.println("place a finger");

        // Set the callback function name for getting captured frame and the information as second parameter
        nRes = p.UFS_StartCapturing(sc.getDeviceHandle(), "captureCallback");
        // or
        //nRes = p.UFS_CaptureSingleImage(sc.getDeviceHandle());
        if (nRes == p.UFS_OK) {
            System.out.println("capture single image success: " + nRes);
        } else {
            System.out.println("capture single image failed: " + nRes);
        }
    }

    public int testCallStartCapturing() {
        int nRes = 0;
        long[] hScanner = new long[1];
        hScanner[0] = currentScanner.getDeviceHandle();

        if (hScanner != null) {
            System.out.println("UFS_StartCapturing,get current scanner handle success! : " + hScanner[0]);
            System.out.println("get Scanner handle success pointer:" + hScanner[0]);
        } else {
            System.out.println("UFS_StartCapturing, GetScannerHandle fail!!");
            System.out.println("UFS_StartCapturing, get Scanner handle fail!!");
            return 0;
        }

        // Set the callback function name for getting captured frame and the information as second parameter
        nRes = p.UFS_StartCapturing(hScanner[0], "captureCallback");

        if (nRes == 0) {
            System.out.println("UFS_StartCapturing success!!");
            nCaptureFlag = 1;
        } else {
            System.out.println("UFS_StartCapturing fail!! code:" + nRes);
        }

        return nRes;
    }

    public static void initArray(int nArrayCnt, int nMaxTemplateSize) {
        if (byteTemplateArray != null) {
            byteTemplateArray = null;
        }

        if (intTemplateSizeArray != null) {
            intTemplateSizeArray = null;
        }

        byteTemplateArray = new byte[nArrayCnt][MAX_TEMPLATE_SIZE];
        intTemplateSizeArray = new int[nArrayCnt];
    }

    public void initVariable(int nFlag) {
        if (nFlag == 1) { //UFS_Init\//
            initFlag = 1;
        } else {
            initFlag = 0;
        }

        nCaptureFlag = 0;

        /*long[] hScanner = new long[1];
        int index = 0;
        p.UFS_GetScannerHandle(index, hScanner);
         */
        int[] pValue = new int[1];

        pValue[0] = 2500;

        int nRes = p.UFS_SetParameter(currentScanner.getDeviceHandle(), p.UFS_PARAM_TIMEOUT, pValue);
        if (nRes == 0) {
            System.out.println("Change combox-timeout,201(timeout) value is " + pValue[0]);
        } else {
            System.out.println("Change combox-timeout,change parameter value fail! code: " + nRes);
        }

        pValue[0] = 100;
        nRes = p.UFS_SetParameter(currentScanner.getDeviceHandle(), p.UFS_PARAM_BRIGHTNESS, pValue);
        if (nRes == 0) {
            System.out.println("Change combox-brightness,202 value is " + pValue[0]);
        } else {
            System.out.println("Change combox-brightness,change parameter value fail! code: " + nRes);
        }

        pValue[0] = 2;
        nRes = p.UFS_SetParameter(currentScanner.getDeviceHandle(), p.UFS_PARAM_DETECT_FAKE, pValue);
        if (nRes == 0) {
            System.out.println("Change combox-detect_fake,312(fake detect) value is " + pValue[0]);
        } else {
            System.out.println("Change combox-detect_fake,change parameter value fail! code: " + nRes);
        }

        pValue[0] = 4;
        nRes = p.UFS_SetParameter(currentScanner.getDeviceHandle(), p.UFS_PARAM_SENSITIVITY, pValue);
        if (nRes == 0) {
            System.out.println("Change combox-sensitivity,203 value is " + pValue[0]);
        } else {
            System.out.println("Change combox-sensitivity,change parameter value fail! code: " + nRes);
        }

        nRes = p.UFS_SetTemplateType(currentScanner.getDeviceHandle(), p.UFS_TEMPLATE_TYPE_SUPREMA); //2001 Suprema type
        if (nRes == 0) {
            System.out.println("Change combox-Scan TemplateType:2001");
        } else {
            System.out.println("Change combox-Scan TemplateType,change parameter value fail! code: " + nRes);
        }
    }

    public void initSDK() {
        int scanners[] = new int[1];

        int nRes = 0;
        nRes = p.UFS_Init();

        if (nRes == p.UFS_OK) {
            // UFS_Init is succeeded

            //List and Init connected scanners
            listAllScanner();

            initFlag = 1;
            p.UFS_SetClassName("com.wonder.javacard.fingerprint.BioCardFX");
            nRes = p.UFS_SetScannerCallback("scannerCallback");
            if (nRes == p.UFS_OK) {
                System.out.println("ScannerCallBack set with success !");

                nRes = p.UFS_GetScannerNumber(scanners);
                if (nRes == p.UFS_OK) {
                    System.out.println("Ressources... " + Arrays.toString(scanners));

                    nRes = p.UFM_Create(hMatcher);

                    if (nRes == p.UFS_OK) {
                        System.out.println("scannerCallBack initialize with success !");
                        initVariable(1);
                        initArray(100, 1024); //array size,template size

                        //security level (1~7)
                        int[] refValue = new int[1];

                        nRes = p.UFM_GetParameter(hMatcher[0], 302, refValue); //302 : security level :UFM_

                        if (nRes == 0) {
                            nSecurityLevel = refValue[0];//
                            System.out.println("get security level,302(security) value is " + nSecurityLevel);
                        } else {
                            System.out.println("get security level fail! code: " + nRes);
                            //MsgBox("get security level fail! code: " + nRes);
                        }

                        //fast mode
                        int[] refFastMode = new int[1];

                        nRes = p.UFM_SetParameter(hMatcher[0], p.UFM_PARAM_FAST_MODE, refFastMode);

                        if (nRes == 0) {
                            nFastMode = refFastMode[0];
                            System.out.println("get fastmode,301(fastmode) value is " + nFastMode);
                            //MsgBox("get fastmode,301(fastmode) value is "+refFastMode.getValue());

                        } else {
                            System.out.println("get fastmode value fail! code: " + nRes);
                            //MsgBox("get fastmode value fail! code: " + nRes);
                        }

                        int nSelectedIdx = 0;

                        if (hMatcher != null) {
                            switch (nSelectedIdx) {

                                case 0:
                                    nRes = p.UFM_SetTemplateType(hMatcher[0], p.UFM_TEMPLATE_TYPE_SUPREMA); //2001 Suprema type

                                    break;
                                case 1:
                                    nRes = p.UFM_SetTemplateType(hMatcher[0], p.UFM_TEMPLATE_TYPE_ISO19794_2); //2002 iso type

                                    break;
                                case 2:
                                    nRes = p.UFM_SetTemplateType(hMatcher[0], p.UFM_TEMPLATE_TYPE_ANSI378); //2003 ansi type

                                    break;
                            }
                        }

                    } else {

                        System.out.println("UFM_Create fail!! code :" + nRes);

                        return;
                    }
                } else {
                    // UFS_Init is failed
                    // Use UFS_GetErrorString method to show error string
                    System.out.println("No Ressource");
                }
            }
        }
    }

    public byte[] extractTemplate(Scanner sc) {
        long[] hScanner = new long[1];
        hScanner[0] = sc.getDeviceHandle();
        byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];

        int[] refTemplateSize = new int[1];

        int[] refTemplateQuality = new int[1];

        int nRes = 0;
        int nSelectedIdx = 0;

        switch (nSelectedIdx) {

            case 0:
                nRes = p.UFS_SetTemplateType(hScanner[0], 2001); //2001 Suprema type
                break;
            case 1:
                nRes = p.UFS_SetTemplateType(hScanner[0], 2002); //2002 iso type
                break;
            case 2:
                nRes = p.UFS_SetTemplateType(hScanner[0], 2003); //2003 ansi type
                break;
        }

        if (nRes == 0) {
            nRes = p.UFS_ExtractEx(hScanner[0], MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize, refTemplateQuality);
        }

        if (nRes == 0) {
        	extractionStatus = true;
            System.out.println("UFS_ExtractEx success!! Size:" + refTemplateSize[0] + "\tQuality:" + refTemplateQuality[0]);
            System.out.println("Template String Value:\t" + Arrays.toString(bTemplate));
            System.out.println(bTemplate);
        } else {
        	extractionStatus = false;
            System.out.println("UFS_ExtractEx fail!! code:" + nRes);
            byte[] refErr = new byte[512];
            nRes = p.UFS_GetErrorString(nRes, refErr);
            String strErr = new String(refErr);

            if (nRes == 0) {
                System.out.println("==>UFS_GetErrorString err is " + strErr);
            }
        }
        return bTemplate;
    }

    public boolean verifyTemplate(Scanner currentScanner) {
        boolean result = false;
        int nSelectedIdx = templateList.size() - 1;

        if (nSelectedIdx == -1) {
            MsgBox("selet enroll id");
            return result;
        }
        // MsgBox(" enroll id:"+nSelectedIdx + " place a finger");
        long[] hScanner = new long[1];
        hScanner[0] = currentScanner.getDeviceHandle();

        if (hScanner == null) {
            MsgBox("getCurrentScannerHandle fail!! ");
            System.out.println("getCurrentScannerHandle fail!! ");
            return result;
        }

        p.UFS_ClearCaptureImageBuffer(hScanner[0]);

        System.out.println("Place a finger");
        MsgBox("Place a finger");

        int nRes = p.UFS_CaptureSingleImage(hScanner[0]);

        if (nRes != 0) {
            System.out.println("caputure single image fail!! " + nRes);
            MsgBox("caputure single image fail!! " + nRes);
            return result;
        }

        byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];
        int[] refTemplateSize = new int[1];

        int[] refTemplateQuality = new int[1];

        int[] refVerify = new int[1];

        nRes = p.UFS_ExtractEx(hScanner[0], MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize, refTemplateQuality);

        if (nRes == 0) {
            try {

                //nRes = p.UFM_Verify(hMatcher[0], bTemplate, refTemplateSize[0], byteTemplateArray[nSelectedIdx], intTemplateSizeArray[nSelectedIdx], refVerify);//byte[][]
                nRes = p.UFM_Verify(hMatcher[0], bTemplate, refTemplateSize[0], templateList.get(nSelectedIdx), templateList.get(nSelectedIdx).length, refVerify);//byte[][]

                if (nRes == 0) {
                    if (refVerify[0] == 1) {
                        System.out.println("verify success!! enroll_id: " + (nSelectedIdx + 1));
                        MsgBox("verify success!! enroll_id: " + (nSelectedIdx + 1));

                    } else {
                        System.out.println("verify fail!! enroll_id: " + (nSelectedIdx + 1));
                        MsgBox("verify fail!! enroll_id: " + (nSelectedIdx + 1));
                    }
                } else {
                    System.out.println("verify fail!! " + nRes);

                    byte[] refErr = new byte[512];
                    nRes = p.UFM_GetErrorString(nRes, refErr);
                    String strErr = new String(refErr);
                    if (nRes == 0) {
                        System.out.println("==>UFM_GetErrorString err is " + strErr);
                        MsgBox("==>UFM_GetErrorString err is " + strErr);
                    }

                }
            } catch (Exception ex) {

            }

        } else {
            System.out.println("extract template fail!! " + nRes);

        }
        return result;

    }

    public void drawCurrentFingerImage() {
        /*test draw image*/
        int[] Resolution = new int[1];
        int[] Height = new int[1];
        int[] Width = new int[1];
        long[] hScanner = new long[1];

        hScanner[0] = currentScanner.getDeviceHandle();

        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        p.UFS_GetCaptureImageBufferInfo(hScanner[0], Width, Height, Resolution);
        byte[] pImageData = new byte[Width[0] * Height[0]];
        p.UFS_GetCaptureImageBuffer(hScanner[0], pImageData);

        BufferedImage buffImage = new BufferedImage(Width[0], Height[0], BufferedImage.TYPE_BYTE_GRAY);
        buffImage.getRaster().setDataElements(0, 0, Width[0], Height[0], pImageData);

        WritableImage image = SwingFXUtils.toFXImage(buffImage, null);
        BioCardFX.imageL = image;
        File file = new File("C:\\Users\\kherman\\Desktop\\java\\temporybio.jpg");
        try {
            ImageIO.write(buffImage, "jpg", file);
        } catch (IOException ex) {
            Logger.getLogger(BioCardFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("File save successful");
        fingerView = new ImageView(image);
        fingerPane.getChildren().add(fingerView);

        if (gc == null) {
            System.out.println("canvasGraphicsContext is null");
            gc = this.canvas.getGraphicsContext2D();
        }
        //gc.drawImage(image, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        imgPanel = new ImagePanel();
        try {
            imgPanel.drawFingerImage(Width[0], Height[0], pImageData);
        } catch (IOException ex) {
            Logger.getLogger(BioCardFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        leftBox.getChildren().add(new Button("Atomic button"));

        fingerFrame.setSize(320, 480);
        fingerFrame.setVisible(true);
        fingerFrame.setContentPane(imgPanel);
        gc.restore();
        System.out.print(pImageData);
    }

    private void listAllScanner() {
        // Retrieve all connected scanners handle
        long[] scannerHandle = new long[1];
        ArrayList<Long> scannerHandleList = new ArrayList<>();
        byte[] scannerId = new byte[512];
        int[] scannerGeneralOutPut = new int[1];

        int i = 0;
        while (p.UFS_GetScannerHandle(i, scannerHandle) == p.UFS_OK) {
            Scanner sc = new Scanner();

            // Scanner Handle
            sc.setDeviceHandle(scannerHandle[0]);

            //Get scanner Index
            if (p.UFS_OK == p.UFS_GetScannerIndex(scannerHandle[0], scannerGeneralOutPut)) {
                sc.setDeviceIndex(scannerGeneralOutPut[0]);
            }

            // Scanner Type
            if (p.UFS_OK == p.UFS_GetScannerType(scannerHandle[0], scannerGeneralOutPut)) {
                sc.setDeviceType(String.valueOf(scannerGeneralOutPut[0]));
            }

            // Scanner Name / Id
            if (p.UFS_GetScannerID(scannerHandle[0], scannerId) == p.UFS_OK) {
                sc.setDeviceName(new String(scannerId));
            }

            scannerList.add(sc);

            System.out.println(scannerHandle[0] + "... Ressource");
            scannerHandleList.add(scannerHandle[0]);

            i++;
        }
        // Set first scanner as current scnner
        if (scannerList.size() > 0) {
            BioCardFX.currentScanner = scannerList.get(0);
        } else {
            System.out.println("No Scanner Connected");
        }
    }

    public void MsgBox(String log) {
        System.out.println(log);
    }
}
