package com.example.usektlinappcopy;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class reversing {

    public String thePlaceOfConvertedAudio = "";

    reversing(String p1, String p2) {
        thePlaceOfConvertedAudio = convertAudio(p1, p2);
    }
        public String convertAudio (String poriFile, String prevFile){

            //逆再生のためにwavファイルの中身を書き換えるアルゴリズム
        //private void reverseSound () throws InterruptedException {
        try {

            //音声ファイルをバイト配列に読み込む
            InputStream in = new FileInputStream(poriFile); //ファイル名の指定
            byte[] music = new byte[in.available()];//バイト配列を作成
            //.available()  この入力ストリームのメソッドの次の呼び出しによって、
            //ブロックせずにこの入力ストリームから読み込むことができる (またはスキップできる) 推定バイト数を返す。
            BufferedInputStream bis = new BufferedInputStream(in, 8000);//ファイルを読み込む
            //BufferedInputStream(`in`, 16000) 指定されたバッファ・サイズを持つBufferedInputStreamを作成し、
            // その引数である入力ストリームinをあとで使用できるように保存する
            DataInputStream dis = new DataInputStream(bis);//ファイルのデータを読み込む
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readByte(); //disのバイト分の配列を用意する
                i++;
            }

            //wavファイルのヘッダー情報以外（実際の波形が記録されている部分）の
            //buffer配列をバイト配列でつくる
            int len = music.length;
            byte[] buff = new byte[len];
            //バイト配列の中身を前後さかさまにする。
            for (int y = 44; y < music.length - 1; y++) {
                buff[len - y - 1] = music[y];
            }

            ////前後さかさまになったbuffer配列を再びmusic配列に格納しなおす。
            for (int y = 44; y < music.length - 1; y++) {
                music[y] = buff[y];
            }



            dis.close();//使用し終わったら終了する

            //前後さかさまになった音を新しいwavファイルに書き込む。
            OutputStream os = new FileOutputStream(prevFile);//書き込み先のディレクトリを指定
            BufferedOutputStream bos = new BufferedOutputStream(os, 8000);//ファイルを読み込む
            DataOutputStream dos = new DataOutputStream(bos);//ファイルのデータを読み込む
            dos.write(music, 0, music.length - 1);//データの書き込み
            dos.flush();//データの書き込み
            dos.flush();//出力ストリームをフラッシュしてバッファリングされていたすべての出力バイトを強制的に書き込む。


            thePlaceOfConvertedAudio = prevFile; //逆再生ファイルの場所を返す

        } catch (IOException ioe) {
            System.out.println("Error");
        }

        return thePlaceOfConvertedAudio;
    }

    }



