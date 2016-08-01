package com.projects.ola.studiaz71;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordActivity extends AppCompatActivity {

    @Bind(R.id.button_start_stop)
    Button button_start_stop;
    @Bind(R.id.button_save)
    Button button_save;
    @Bind(R.id.button_delete)
    Button button_delete;
    @Bind(R.id.button_go_to_list)
    Button button_go_to_list;
    @Bind(R.id.button_odtworz)
    Button button_odtworz;
    @Bind(R.id.edit_text_imie)
    EditText edit_text_imie;
    @Bind(R.id.edit_text_nazwisko)
    EditText edit_text_nazwisko;
    @Bind(R.id.edit_text_tytul)
    EditText edit_text_tytul;
    @Bind(R.id.edit_text_opis)
    EditText edit_text_opis;

    AudioRecord audioRecord;
    boolean isRecording;
    byte[] buffer;
    Thread recordingThread;
    public int minBuffer;
    List<byte[]> listaNagran;
    int byteSizeActual;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        button_start_stop.setText("START");
        minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        listaNagran=new ArrayList<>();
        edit_text_tytul.setText("");
        edit_text_imie.setText("");
        edit_text_nazwisko.setText("");
        edit_text_opis.setText("");
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, "OLA_FolderDoAp7");
    }

 //zamiast buffera ewentualnie http://www.codota.com/android/scenarios/52fcbd68da0a12c570f3be72/android.media.AudioRecord?tag=dragonfly
    //http://www.codota.com/android/scenarios/52fcbd68da0a12c570f3be72/android.media.AudioRecord?tag=dragonfly

    @OnClick(R.id.button_start_stop)
 public void startStopMethod(){
     if(button_start_stop.getText().equals("START")){
         audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
         button_start_stop.setText("STOP");
         audioRecord.startRecording();
         isRecording=true;
         byteSizeActual=0;
         recordingThread=new Thread(new Runnable() {
             @Override
             public void run() {
                 while(isRecording){
                     buffer= new byte[minBuffer * 100];
                     int bufferReadResult=audioRecord.read(buffer,0,buffer.length);
                     if(AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult){
                        // int foundPeak=searchThreshold(buffer, (byte) 15000);
                         // if(foundPeak>-1) {
                         listaNagran.add(buffer);
                         //System.out.println("DODANO");
                         //byteSizeActual += buffer.length;
                         //}
                         // listaNagran.add(searchThreshold(buffer, (byte) 15000));
                     }
                 }
             }
         });
         recordingThread.start();
     }else{
         isRecording=false;
         button_start_stop.setText("START");
         recordingThread.interrupt();
         audioRecord.stop();
         audioRecord.release();
     }
 }



    @OnClick(R.id.button_odtworz)
    public void odtworz(){
        if(!listaNagran.isEmpty()) {
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 200), AudioTrack.MODE_STATIC);
            // track.write(listaNagran.get(0), 0, listaNagran.get(0).length);
            //track.play();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                for (int i = 0; i < listaNagran.size(); i++) {
                    outputStream.write(listaNagran.get(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte data[] = outputStream.toByteArray();

            track.write(data, 0, data.length);
            track.play();
        }else{
            Toast.makeText(this, "Nie ma nagrania", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_save)
    public void zapisz(){
        //writeAudioDataToFile();
 //----------------------------------
        if (!listaNagran.isEmpty()) {
            Date cDate = new Date();
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate)+",godz"+rightNow.get(Calendar.HOUR_OF_DAY)+"."+rightNow.get(Calendar.MINUTE)+"."+rightNow.get(Calendar.SECOND);
            String tytul = edit_text_imie.getText() + "_" + edit_text_nazwisko.getText() + "_" + edit_text_tytul.getText() + "_" + fDate ;
            String miejsceZapisu = getFilename() +"/";
            writeAudioDataToFile(miejsceZapisu, tytul+".wav");
           zapiszTytulDoTxT(tytul);
            Toast.makeText(this, "Nagranie zostało zapisane", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Nie ma nagrania", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_delete)
    public void usun(){
        listaNagran.clear();
        byteSizeActual=0;
        Toast.makeText(this, "Nagranie zostalo skasowane", Toast.LENGTH_LONG).show();
        edit_text_imie.setHint(R.string.edit_text_imie);
        edit_text_nazwisko.setHint(R.string.edit_text_nazwisko);
        edit_text_tytul.setHint(R.string.edit_text_tytul);
        edit_text_opis.setHint(R.string.edit_text_opis);
        edit_text_tytul.setText("");
        edit_text_imie.setText("");
        edit_text_nazwisko.setText("");
        edit_text_opis.setText("");

    }

    @OnClick(R.id.button_go_to_list)
    public void idzDoListy(){
        startActivity(new Intent(this, ListActivity.class));
    }
//--------------------------------------------------------------------------------------------

   // zapisywanie
    //http://www.edumobile.org/android/audio-recording-in-wav-format-in-android-programming/
private String getFilename(){
    if(!file.exists()){
        file.mkdirs();
    }
    return (file.getAbsolutePath()+"");
}

    private void zapiszTytulDoTxT( String tytul) {
        String state = Environment.getExternalStorageState();
        OutputStream outStream = null;

        try {
            outStream = new FileOutputStream(new File(file.getAbsolutePath(),"listaTytulow.txt"), true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(outStream);
            myOutWriter.write(tytul+"/// "+edit_text_opis.getText());
            myOutWriter.write("\n");
            myOutWriter.close();
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Nie znaleziono pliku", Toast.LENGTH_LONG).show();
        }catch(OutOfMemoryError E) {
            Toast.makeText(this,"Masz za malo miejsca w pamieci, usun cos",Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            Toast.makeText(this, "Inny wyjatek", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    int searchThreshold(byte[] arr, byte thr){
//        int peakIndex;
//        int arrLen=arr.length;
////        for(peakIndex=0;peakIndex<100;peakIndex++){
////            System.out.println(peakIndex+"     "+arr[peakIndex]+"    <-----------------------------");
////        }
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////        try {
////            for(int i=0;i<listaNagran.size();i++) {
////                outputStream.write( listaNagran.get(i) );
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        int iloscZer=0;
//        //int zero=];
//        //zero[0]=(byte) 0;
//        byte[] pomocnicza=new byte[10000];
//        int indTabPom=0;
//        for (peakIndex=0;peakIndex<arrLen;peakIndex++){
//          //  System.out.println(peakIndex+" "+arr[peakIndex]+"    <--------------------------------");
////            if ((arr[peakIndex]>=thr) || (arr[peakIndex]<=-thr)){
////               // return peakIndex;
////            }
//            int l=arr[peakIndex];
//            if((l==0)){
//                iloscZer++;
//                pomocnicza[iloscZer]=arr[peakIndex];
//                indTabPom++;
//            }else{
//                iloscZer=0;
//                pomocnicza=new byte[10000];
//                pomocnicza[indTabPom]=arr[peakIndex];
//                indTabPom++;
//            }
//            if(iloscZer==9999){
//                pomocnicza=new byte[10000];
//                iloscZer=0;
//                indTabPom=0;
//            }
//            if(indTabPom==9999){
//                try {
//                    outputStream.write( pomocnicza );
//                    AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 200), AudioTrack.MODE_STATIC);
//                    track.write(pomocnicza, 0, pomocnicza.length);
//                    track.play();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                pomocnicza=new byte[10000];
//                iloscZer=0;
//                indTabPom=0;
//            }
//        }
////        if(pomocnicza.length>0){
////            try {
////                outputStream.write( pomocnicza );
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//        byte data[] = outputStream.toByteArray( );
//        System.out.println(data.length+"          DLUGOSCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCcc");
//        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 200), AudioTrack.MODE_STATIC);
//        track.write(data, 0, data.length);
//        track.play();

        int peakIndex;
        int arrLen=arr.length;
        for (peakIndex=0;peakIndex<arrLen;peakIndex++){
            //if ((arr[peakIndex]>=thr) || (arr[peakIndex]<=-thr)){
                //se supera la soglia, esci e ritorna peakindex-mezzo kernel.
            if(arr[peakIndex]!=0){
                return peakIndex;
            }
        }
        return -1; //not found
    }

    private void writeAudioDataToFile(String miejsceZap, String tyt){
        //String outFilename = getFilename();
        //FileOutputStream out = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for(int i=0;i<listaNagran.size();i++) {
                outputStream.write( listaNagran.get(i) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte data[] = outputStream.toByteArray( );
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = 44100;
        int channels = 1;
        long byteRate = 16 * 44100 * channels/8;
        FileOutputStream os = null;
        totalAudioLen = data.length;
        totalDataLen = totalAudioLen + 36;
        try {
            os = new FileOutputStream(miejsceZap+tyt);
            WriteWaveFileHeader(os, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            os.write(data);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
