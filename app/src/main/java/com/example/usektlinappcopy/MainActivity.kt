package com.example.usektlinappcopy

//録音とアプリの処理の中心

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.view.View
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.IOException
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import java.io.*
import kotlin.Throws

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {

    //フィールド変数部
    private var editText: EditText? = null //変換したい文字列を入力する場所
    private var convertButton: Button? = null //変換開始ボタン
    private var textView: TextView? = null //変換前を表示するスペース
    private var textView2: TextView? = null //変換元を表示するスペース
    var hiragana2romaji //メインの処理を行うクラス
            : Hira2Roma01? = null
    var reverseAudio //メインの処理を行うクラス
            : reversing? = null
    var text = "" //Hira2Romaに渡す文字列

    //val filePath = "/storage/emulated/0/Download/sampleWav.wav" //録音用のファイルパス


    private var recorder: MediaRecorder? = null //メディアレコーダーの初期化
    private var fileName: String = "" //あとから読み込む順再生のファイルのパスを指定する
    private var revFileName: String = "" //あとから読み込む逆再生のファイルのパスを指定する

    private var player: MediaPlayer? = null //メディアプレイヤーの初期化

    //録音の許可（始めはfalse）
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)




    override fun onRequestPermissionsResult( //マイクの使用許可を取る
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED //bool値を求め、permissionToRecordAcceptedに代入
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish() //permissionToRecordAcceptedがtrueならオブジェクト取得->リスナのインスタンス生成->リスナの設定と処理が進む
    }

    //録音の開始
    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    //順音声ファイルの再生の開始
    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }


    //逆音声ファイルの再生の開始
    private fun onRevPlay(start: Boolean) = if (start) {
        startReversePlaying()
    } else {
        stopPlaying()
    }


    //順再生を開始する準備
    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName) //再生するファイルを指定
                prepare() //メディアプレイヤーの準備
                start() //実行する
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }


    //逆再生を開始する準備
    private fun startReversePlaying() {
        player = MediaPlayer().apply {
            try {
                reverseAudio = reversing(fileName,revFileName)

                setDataSource(revFileName) //再生するファイルを指定
                prepare() //メディアプレイヤーの準備
                start() //実行する
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }


    //再生を停止する
    private fun stopPlaying() {
        player?.release() //メディアプレイヤーの解放
        player = null //再びnull
    }




    //録音を開始する。
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) //入力ソースを指定する


            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT) //エンコード方式(DEFAULTはwav)を指定
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT) //オーディオエンコーダを指定
            //setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) //エンコード方式(3gpなど)を指定
            //setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //オーディオエンコーダを指定


            setOutputFile(fileName) //出力先設定(パス)



            try {
                prepare() //オーディオレコーダーの準備を行う
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start() //録音の開始
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop() //録音の停止
            release() //MediaRecorderオブジェクトの解放(再利用不可)
        }
        recorder = null
    }

    //superクラスにある同じ名前のメソッドを上書き
    override fun onCreate(savedInstanceState: Bundle?) { //インスタンス時に必ず実行されるメソッド
        fileName = "${externalCacheDir?.absolutePath}/forwardWav.wav" //録音したデータを符号化して保存する
        revFileName = "${externalCacheDir?.absolutePath}/reverseWav.wav" //逆再生の録音用のファイルパス、録音したデータを符号化して保存する
        //fileName = "/storage/emulated/0/Download/forwardWav.wav" //順再生の録音用のファイルパス、録音したデータを符号化して保存する
        //revFileName = "/storage/emulated/0/Download/reverseWav.wav" //逆再生の録音用のファイルパス、録音したデータを符号化して保存する
        super.onCreate(savedInstanceState) //親クラスのメソッドを使用
        setContentView(R.layout.activity_main) //activity_main.xmlをレイアウトとして登録する。
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION) //マイクの使用許可をとる


        val record = findViewById<Button>(R.id.record) //録音オブジェクト取得
        val stop = findViewById<Button>(R.id.stop) //録音停止オブジェクト取得
        val playback = findViewById<Button>(R.id.playback) //順再生オブジェクト取得
        val reverseaudio = findViewById<Button>(R.id.reverseaudio) //逆再生オブジェクト取得

        val listener = RecordButton() //レコードボタンリスナのインスタンス生成

        record.setOnClickListener(listener) //レコードボタンリスナの設定
        stop.setOnClickListener(listener) //ストップボタンリスナの設定
        playback.setOnClickListener(listener) //順再生ボタンリスナの設定
        reverseaudio.setOnClickListener(listener) //逆再生ボタンリスナの設定


        //変数 = (キャスト)findViewById(使いたいウィジェットのid(int型))
        //findViewByIdは「idからビューを見つける」という意味
        editText = findViewById<View>(R.id.editText) as EditText
        convertButton = findViewById<View>(R.id.convertButton) as Button
        textView = findViewById<View>(R.id.textView) as TextView
        textView2 = findViewById<View>(R.id.textView2) as TextView

        //ボタンにリスナー(その動作、今回で言えばボタンが押された時に呼ばれるやつ)を設定する。
        convertButton!!.setOnClickListener { view ->
            //以下が押された時の処理
            val text = editText!!.text.toString() //EditText(テキストボックス)から文字列を取得
            if (text != "") { //もし文字列textが空白("")でなかったら
                textView!!.text = text //TextViewに文字列をセット
                hiragana2romaji = Hira2Roma01(text) //Hira2Roma01クラスを使う
                //System.out.println("受け取ったMAINACTIVITY:"+hiragana2romaji.finalResult);
                textView2!!.text = hiragana2romaji!!.finalResult //TextView2に文字列をセット
                editText!!.setText("") //テキストボックスを初期化
                //トースト(警告メッセージ等に用いられる一時的な表示)
                //Toast.makeText(コンテキスト, 表示したい文字列, どれぐらい(時間)表示させたいか(LONG or SHORT)).show();
                Toast.makeText(view.context, "変換完了", Toast.LENGTH_LONG).show()

                //ダイアログ(ポップアップメッセージ)を表示
                AlertDialog.Builder(view.context)
                    .setTitle("逆再生の発音") //タイトル
                    .setMessage(hiragana2romaji!!.finalResult) //本文
                    .setPositiveButton("OK", null) //押したら閉じるボタンみたいなやつのテキスト(第一引数)
                    .show()
            } else {
                Toast.makeText(view.context, "入力してください", Toast.LENGTH_SHORT).show()
            }
        }
    }





    //クリックイベントの設定
    private inner class RecordButton : View.OnClickListener {
        override fun onClick(v: View?) {
            Log.i(LOG_TAG, "クリック成功")
            Log.i(LOG_TAG, fileName)

            if(v != null){
                when(v.id){
                    //録音開始ボタン
                    R.id.record -> {
                        onRecord(true)
                        Log.i(LOG_TAG, "録音開始")
                    }
                    //録音停止ボタン
                    R.id.stop -> {
                        onRecord(false)
                        Log.i(LOG_TAG, "録音終了")
                    }
                    //順方向への再生ボタン
                    R.id.playback -> {
                        onPlay(true)
                        Log.i(LOG_TAG, "順再生中")
                    }

                    //逆方向への再生ボタン
                    R.id.reverseaudio -> {
                        onRevPlay(true)
                        Log.i(LOG_TAG, "逆再生中")
                    }


                }
            }
        }
    }
}