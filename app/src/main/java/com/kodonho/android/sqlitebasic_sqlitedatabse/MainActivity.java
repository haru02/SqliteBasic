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
    Button btnInsert;
    Button btnSelect;
    Button btnUpdate;
    Button btnDelete;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        openDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터베이스를 연결하는 Api
                db = SQLiteDatabase.openDatabase(getFullpath("sqlite.db"),null,0);
                // 0: 쓰기가능 1: read only
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db != null){
                    // 쿼리를 실행해준다. select 문을 제외한 모든 쿼리에 사용
                    db.execSQL("insert into bbs(no,name,title) values(1,'홍길동','글제목')");
                    // 쿼리를 실행 후 결과값을 Cursor 리턴해 준다 즉... select문에 사용
                    //db.rawQuery("",null);
                }
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db !=null){
                    Cursor cursor = db.rawQuery("select * from bbs",null);
                    while(cursor.moveToNext()){
                        int idx = cursor.getColumnIndex("no"); // 컬럼명에 해당하는 순서를 가져온다
                        String id = cursor.getString(idx); // 순서로 컬럼을 가져온다
                        idx = cursor.getColumnIndex("name");
                        String name = cursor.getString(idx);
                        idx = cursor.getColumnIndex("title");
                        String title = cursor.getString(idx);

                        result.setText("id="+id+", name="+name+", title="+title);
                    }
                }
            }
        });

    }

    private void init(){
        assetToDisk("sqlite.db");

        result = (TextView) findViewById(R.id.textView);
        openDatabase = (Button) findViewById(R.id.btnOpen);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
    }

    // 파일이름을 입력하면 내장 디렉토리에 있는 파일의 전체경로를 리턴해준다
    public String getFullpath(String fileName){
        return getFilesDir().getAbsolutePath() + File.separator + fileName;
    }

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
