package com.kodonho.android.sqlitebasic_sqlitedatabse;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    TextView result;

    Button openDatabase;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void onInsert(View v) {
        SQLiteDatabase db = null;
        try {
            db = openDatabase();
            if (db != null) {
                // 쿼리를 실행해준다. select 문을 제외한 모든 쿼리에 사용
                db.execSQL("insert into bbs(no,name,title) values(1,'홍길동','글제목')");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (db != null) db.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    // 데이터베이스를 연결하는 Api
    public SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openDatabase(getFullpath("sqlite.db"), null, 0); // 0: 쓰기가능 1: read only
    }

    public void onSelect(View v) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openDatabase();
            if (db != null) {
                cursor = db.rawQuery("select * from bbs order by no", null);
                while (cursor.moveToNext()) {
                    int idx = cursor.getColumnIndex("no"); // 컬럼명에 해당하는 순서를 가져온다
                    String id = cursor.getString(idx); // 순서로 컬럼을 가져온다
                    idx = cursor.getColumnIndex("name");
                    String name = cursor.getString(idx);
                    idx = cursor.getColumnIndex("title");
                    String title = cursor.getString(idx);
                    String temp = result.getText().toString();
                    result.setText(temp + "\n id=" + id + ", name=" + name + ", title=" + title);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (cursor != null) cursor.close();
                if (db != null) db.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void init(){
        // 파일이 있으면 덮어쓰지 않는다
        File file = new File(getFullpath("sqlite.db"));
        if(!file.exists())
            assetToDisk("sqlite.db");

        result = (TextView) findViewById(R.id.textView);
    }

    // 파일이름을 입력하면 내장 디렉토리에 있는 파일의 전체경로를 리턴해준다
    public String getFullpath(String fileName){
        // internal 디렉토리중 files 디렉토리의 경로를 가져온다
        return getFilesDir().getAbsolutePath() + File.separator + fileName;
    }

    // assets 에 있는 파일을 쓰기가능한 disk 디렉토리로 복사한다
    // 안드로이드 internal Disk 는 cache, files 등 쓰기가능한 폴더가 정해져 있다
    public void assetToDisk(String fileName){
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            // 외부에서 작성된 sqlite db파일 사용하기
            // 1. assets 에 담아둔 파일을 internal 혹은 external
            //    공간으로 복사하기 위해 읽어온다
            AssetManager manager = getAssets();
            // assets 에 파일이 없으면 exception 이 발생하여 아래 로직이 실행되지 않는다
            is = manager.open(fileName);
            bis = new BufferedInputStream(is);
            // 2. 저장할 위치에 파일이 없으면 생성한다
            String targetFile = getFullpath(fileName);
            File file = new File(targetFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 3. outputStream 을 생성해서 파일내용을 쓴다
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            // 읽어올 데이터를 담아줄 변수
            int read = -1; // 모두 읽어오면 -1이 리턴된다
            // 한번에 읽을 버퍼의 크기를 지정
            byte buffer[] = new byte[1024];
            // 더 이상 읽어올 데이터가 없을때까지 buffer 단위로 읽어서 쓴다
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }
            // 남아 있는 데이터를 buffer에서 써준다
            bos.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                // 작업이 완료되면 모든 stream을 닫아준다
                if (bos != null) bos.close();
                if (fos != null) fos.close();
                if (bis != null) bis.close();
                if (is != null) is.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
