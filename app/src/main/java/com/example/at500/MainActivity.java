package com.example.at500;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Import the necessary classes.
import com.keyence.autoid.sdk.scan.DecodeResult;
import com.keyence.autoid.sdk.scan.ScanManager;
import com.keyence.autoid.sdk.scan.scanparams.ScanParams;
import com.keyence.autoid.sdk.scan.scanparams.scanParams.Collection;
import com.keyence.autoid.sdk.scan.scanparams.scanParams.Target;
import com.keyence.autoid.sdk.scan.scanparams.scanParams.Trigger;

import java.util.List;

// Add an event listener.
public class MainActivity extends AppCompatActivity implements ScanManager.DataListener {
    private ScanManager mScanManager;

    public List<String> codeTypeList;
    public List<String> dataList;
    public int i = 0;

    // Create a read event.
    @Override
    public void onDataReceived(DecodeResult decodeResult) {
        TextView barcode1Textview = (TextView) findViewById(R.id.barcode);
        TextView format1Textview = (TextView) findViewById(R.id.format);
        TextView barcode2Textview = (TextView) findViewById(R.id.barcode2);
        TextView format2Textview = (TextView) findViewById(R.id.format2);


        // Acquire the reading result.
        DecodeResult.Result result = decodeResult.getResult();
/*
        // Acquire the read code type.
        String codeType = decodeResult.getCodeType();
        // Acquire the read data.
        String data = decodeResult.getData();
        barcodeTextview.setText(data);
        formatTextview.setText(codeType);
*/

        // Acquire the read code type list.
        codeTypeList = decodeResult.getCodeTypeList();
        // Acquire the read data list.
        dataList = decodeResult.getDataList();

        int retval = codeTypeList.size();
         if(retval == 1){
             format1Textview.setText(codeTypeList.get(0));
             barcode1Textview.setText(dataList.get(0));

             format2Textview.setText(format2Textview.getHint());
             barcode2Textview.setText(barcode2Textview.getHint());
             System.out.println("Normal Scan");
         }else if (retval == 2){
             format1Textview.setText(codeTypeList.get(0));
             barcode1Textview.setText(dataList.get(0));

             format2Textview.setText(codeTypeList.get(1));
             barcode2Textview.setText(dataList.get(1));
             System.out.println("2 Scan");

             //Reset Default
             resumeScanDefault();
         }else {
             //Reset Default
             resumeScanDefault();
         }

    }
    public void resumeScanDefault(){
        //Resume default

        ScanParams scanParams = new ScanParams();
        mScanManager.getConfig(scanParams);
        //Set timeout
        scanParams.trigger.scannerTimeout  = 25;

        //Scan Trigger
        scanParams.trigger.triggerMode = Trigger.TriggerMode.NORMAL;

        //Collection
        scanParams.collection.method = Collection.Method.ACCUMULATE;
        scanParams.collection.codeCountReadAtOnce = 1;
        scanParams.collection.codeCountAccumulate = 1;
        scanParams.collection.rejectSameCode = true;
        mScanManager.setConfig(scanParams);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a ScanManager class instance.
        mScanManager = ScanManager.createScanManager(this);
        // Create a listener to receive a read event.
        mScanManager.addDataListener(this);

        Button btblockscan = (Button) findViewById(R.id.blockscan);
        btblockscan.setOnClickListener(btblockscanListener);

        Button btresumescan = (Button) findViewById(R.id.resumescan);
        btresumescan.setOnClickListener(btresumescanListener);

        Button bttwoscan = (Button) findViewById(R.id.twoscan);
        bttwoscan.setOnClickListener(bttwocanListener);


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Discard the ScanManager class instance.
        mScanManager.removeDataListener(this);
        // Discard the ScanManager class instance to release the resources.
        mScanManager.releaseScanManager();
        mScanManager.unlockScanner();
    }

    private Button.OnClickListener btblockscanListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mScanManager.lockScanner();
        }
    };
    private Button.OnClickListener btresumescanListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mScanManager.unlockScanner();
        }
    };
    private Button.OnClickListener bttwocanListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            ScanParams scanParams = new ScanParams();

            mScanManager.getConfig(scanParams);

            //Set timeout
            scanParams.trigger.scannerTimeout  = 10;

            //Scan Trigger
            scanParams.trigger.triggerMode = Trigger.TriggerMode.TRIGGER_AT_RELEASE;

            //Collection
            scanParams.collection.method = Collection.Method.READ_AT_ONCE;
            scanParams.collection.codeCountReadAtOnce = 2;

            scanParams.collection.rejectSameCode = true;
            mScanManager.setConfig(scanParams);
            mScanManager.startRead();

        }
    };


}
