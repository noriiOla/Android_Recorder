package com.projects.ola.studiaz71;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class connected_activity extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    @Bind(R.id.list_view_polaczenieNagran)
    ListView listaNagran;

    File file;
    int indeksPierwszy;
    String[] listaDoWysw;
    int indPierwszy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_activity);
        ButterKnife.bind(this);
        //listaNagran= (ListView) findViewById(R.id.list_view_polaczenieNagran);
        Bundle bundle = getIntent().getExtras();
        indeksPierwszy=bundle.getInt("Indeks_pierwszy");
        indPierwszy=indeksPierwszy;
        String filepath = Environment.getExternalStorageDirectory().getPath();
         file = new File(filepath, "OLA_FolderDoAp7");

         ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw());
          listaNagran.setAdapter(adap);
        listaNagran.setOnItemClickListener(this);
        Toast.makeText(this,"Wybierz drugi plik do polaczenia", Toast.LENGTH_SHORT).show();
    }

    public boolean sprawdzAktualnoscListy(){
        boolean bylyZmiany=false;
        try {
            File inputFile = new File(file.getAbsolutePath(),"listaTytulow.txt");
            File tempFile = new File(file.getAbsolutePath(), "temp_ file.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            int ind = 0;
            String lin = "";
            while ((lin = reader.readLine()) != null) {
                File fff=new File( file.getAbsolutePath(), lin.split("///")[0]+".wav");
                if (fff.exists()) {
                    writer.write(lin);
                    writer.write("\n");
                }else{
                    bylyZmiany=true;
                }
                ind++;
            }

            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(inputFile);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(OutOfMemoryError E) {
            Toast.makeText(this,"Masz za malo miejsca w pamieci, usun cos",Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Wysypalo przy dodawaniju", Toast.LENGTH_SHORT).show();
        }
        return bylyZmiany;
    }

    public String[] stworzListeDoWysw(){
        sprawdzAktualnoscListy();
        int iloscLini=0;
        FileInputStream fileIn=null;
        File f=new File(file.getAbsolutePath(),"listaTytulow.txt");
        try{
            fileIn=new FileInputStream(f);
            InputStreamReader InputRead=new InputStreamReader(fileIn);
            BufferedReader buffreader = new BufferedReader(InputRead);
            String line="";
            while ((line=buffreader.readLine())!=null) {
                iloscLini++;
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        iloscLini--;
        listaDoWysw=new String [iloscLini];
        try{
            fileIn=new FileInputStream(f);
            InputStreamReader InputRead=new InputStreamReader(fileIn);
            BufferedReader buffreader = new BufferedReader(InputRead);
            String line="";

            int ind=0;

            while ((line=buffreader.readLine())!=null) {
                if(!(ind==indeksPierwszy)) {
                    listaDoWysw[ind] = line.split("///")[0];
                    ind++;
                }else{
                    indeksPierwszy=-1;
                }
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaDoWysw;
    }

  //  @OnClick(R.id.button_polacz_pliki)
    public void polaczPliki(){
        boolean bylyZmiany = sprawdzAktualnoscListy();
        if(!bylyZmiany) {
            //zrobNowyPlik
            //usunWszystkie z indeksow
            //zbierzIndeksy
        }else{
            Toast.makeText(this, "Akcja została zatrzymana ponieważ lista posiadała nieakutalne dane. Lista została zaktualizowana. Wybierz plik ponownie", Toast.LENGTH_LONG).show();
            listaNagran.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw()));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String nazwaPierwszego=znajdzPlik(indPierwszy);
        String[] pfirst=nazwaPierwszego.split("///");
        String nazwaWavPliku=pfirst[0];
        System.out.println("PIERWSZY PLIK!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+file.getAbsolutePath()+nazwaWavPliku);
        File f=new File(file.getAbsolutePath(),nazwaWavPliku+".wav");
        byte[] pierwszyPlik=new byte[(int)f.length()];
        InputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fis.read(pierwszyPlik, 0, pierwszyPlik.length);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //---------------------------------------------------------
        //System.out.println(listaDoWysw.length+" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  "+position+" !!!!!!!!!!!"  );
        File fDrugi=new File(file.getAbsolutePath(),listaDoWysw[position]+".wav");

        System.out.println("PIERWSZY PLIK!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+listaDoWysw[position]+".wav");

        byte[] drugiPlik=new byte[(int)f.length()];
        InputStream fisDrugi = null;
        try {
            fisDrugi = new FileInputStream(fDrugi);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fisDrugi.read(drugiPlik, 0, drugiPlik.length);
            fisDrugi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-----------------------------------------------------
        f.delete();
        fDrugi.delete();
        usunZTxt(listaDoWysw[position]);
        usunZTxt(indPierwszy);
         int  minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
       // AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 200), AudioTrack.MODE_STATIC);
        // track.write(listaNagran.get(0), 0, listaNagran.get(0).length);
        //track.play();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(pierwszyPlik);
            outputStream.write(drugiPlik);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte data[] = outputStream.toByteArray();
        zapiszTytulDoTxT(nazwaWavPliku, pfirst[1]);
        writeAudioDataToFile(file.getAbsolutePath(),nazwaWavPliku+".wav",data);
        startActivity(new Intent(this, ListActivity.class));
        //track.write(data, 0, data.length);
       // track.play();
    }

    public String znajdzPlik(int position){
        FileInputStream fileIn=null;
        File f=new File(file.getAbsolutePath(),"listaTytulow.txt");
        String lin="";
        try{
            fileIn=new FileInputStream(f);
            InputStreamReader InputRead=new InputStreamReader(fileIn);
            BufferedReader buffreader = new BufferedReader(InputRead);
            int ind = 0;
            while ((lin = buffreader.readLine()) != null) {
                if (ind ==position) {
                    break;
                }
                ind++;
            }
            buffreader.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Wysypalo sie", Toast.LENGTH_SHORT).show();
        }
        return lin;
    }

    private void zapiszTytulDoTxT( String tytul, String opis) {
        String state = Environment.getExternalStorageState();
        OutputStream outStream = null;

        try {
            outStream = new FileOutputStream(new File(file.getAbsolutePath(),"listaTytulow.txt"), true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(outStream);
            myOutWriter.write(tytul+"/// "+opis);
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


    private void writeAudioDataToFile(String miejsceZap, String tyt, byte[] data){
        //String outFilename = getFilename();
        //FileOutputStream out = null;

        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = 44100;
        int channels = 1;
        long byteRate = 16 * 44100 * channels/8;
        FileOutputStream os = null;
        totalAudioLen = data.length;
        totalDataLen = totalAudioLen + 36;
        try {
            os = new FileOutputStream(miejsceZap+"/"+tyt);
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

    public void usunZPliku(String nazwa){
        File f = new File(file.getAbsolutePath(), nazwa);
        f.delete();
    }

    public void usunZTxt(int indLiniDoUsuniecia){
        try {
            File inputFile = new File(file.getAbsolutePath(),"listaTytulow.txt");
            File tempFile = new File(file.getAbsolutePath(), "temp_ file.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            int ind = 0;
            String lin = "";
            while ((lin = reader.readLine()) != null) {
                if (ind != indLiniDoUsuniecia) {
                    writer.write(lin);
                    writer.write("\n");
                }
                ind++;
            }

            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(inputFile);

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(OutOfMemoryError E) {
            Toast.makeText(this,"Masz za malo miejsca w pamieci, usun cos",Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Wysypalo przy dodawaniju", Toast.LENGTH_SHORT).show();
        }

    }

    public void usunZTxt(String tytulDoUsuniecia){
        try {
            File inputFile = new File(file.getAbsolutePath(),"listaTytulow.txt");
            File tempFile = new File(file.getAbsolutePath(), "temp_ file.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            int ind = 0;
            String lin = "";
            while ((lin = reader.readLine()) != null) {
                if (!(lin .equals(tytulDoUsuniecia))) {
                    writer.write(lin);
                    writer.write("\n");
                }
                ind++;
            }

            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(inputFile);

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(OutOfMemoryError E) {
            Toast.makeText(this,"Masz za malo miejsca w pamieci, usun cos",Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Wysypalo przy dodawaniju", Toast.LENGTH_SHORT).show();
        }

    }
}
