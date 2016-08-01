package com.projects.ola.studiaz71;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener{

@Bind(R.id.list_view_nagrania)
    ListView listaNagran;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, "OLA_FolderDoAp7");

        ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw());
       // listaNagran.setOnItemLongClickListener(this);
        listaNagran.setOnItemClickListener(this);
        listaNagran.setAdapter(adap);
        registerForContextMenu(listaNagran);
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
        String[] listaDoWysw;
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
        listaDoWysw=new String [iloscLini];
        try{
            fileIn=new FileInputStream(f);
            InputStreamReader InputRead=new InputStreamReader(fileIn);
            BufferedReader buffreader = new BufferedReader(InputRead);
            String line="";

            int ind=0;

            while ((line=buffreader.readLine())!=null) {
                listaDoWysw[ind]=line.split("///")[0];
                ind++;
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),"Dane zostaly pobrane z External Storage" ,Toast.LENGTH_SHORT).show();
    return listaDoWysw;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        znajdzPlik(position);
        String s= file.getAbsolutePath()+"/"+znajdzPlik(position);
        String sciezka=s.split("///")[0]+".wav";
        System.out.println(sciezka);
       MediaPlayer mMediaPlayer= new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(sciezka);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list_view_nagrania) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(znajdzPlik(info.position));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_kont, menu);
        }
    }

    public void dajInfoOZaniechaniuAkcjiBoListaZostalaZakutalizowana(){
        Toast.makeText(this, "Akcja została zatrzymana ponieważ lista posiadała nieakutalne dane. Lista została zaktualizowana. Wybierz plik ponownie", Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String ss=znajdzPlik(info.position);
        String[] s=ss.split("///");
        boolean bylyZmiany;
        switch(item.getTitle().toString()) {
            case "Pokaz opis":
                bylyZmiany = sprawdzAktualnoscListy();
                if(!bylyZmiany) {
                    Toast.makeText(this, s[1] , Toast.LENGTH_SHORT).show();
                }else{
                    dajInfoOZaniechaniuAkcjiBoListaZostalaZakutalizowana();
                    listaNagran.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw()));
                }
                break;
            case "Polacz":
                bylyZmiany = sprawdzAktualnoscListy();
                if(!bylyZmiany) {
                    Intent dalej=new Intent(this, connected_activity.class);
                    dalej.putExtra("Indeks_pierwszy", info.position);
                    startActivity(dalej);
                }else{
                    dajInfoOZaniechaniuAkcjiBoListaZostalaZakutalizowana();
                    listaNagran.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw()));
                }
                    break;
            case "Usun":
                bylyZmiany = sprawdzAktualnoscListy();
                if(!bylyZmiany) {
                    usunZPliku(s[0]);
                    usunZTxt(info.position);
                    Toast.makeText(this, "Nagranie zostało usuniete", Toast.LENGTH_SHORT).show();
                }else{
                    dajInfoOZaniechaniuAkcjiBoListaZostalaZakutalizowana();
                    listaNagran.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw()));
                }
                    break;
            case "Odswierz liste":
                    //sprawdzAktualnoscListy();
                listaNagran.setAdapter( new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw()));
                Toast.makeText(this, "Lista została odswierzona", Toast.LENGTH_SHORT).show();
                    break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

        listaNagran.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stworzListeDoWysw()));
    }
}
