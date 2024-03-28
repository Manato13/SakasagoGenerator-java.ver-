package com.example.usektlinappcopy;
//文字列を変換するクラス

import java.util.*; //ArrayList クラス、HashMapクラスを利用するため java.utilをインポート


public class Hira2Roma01{

    //フィールド変数
    ArrayList<String> hiralines;  //ArrayListクラスは要素の数が可変のコレクション
    HashMap<String, String> hira2romamap;  //mapは2つの紐付けられたデータを格納できる@型指定<ひらがな,ローマ字>
    HashMap<String, String> roma2hiramap;  //mapは2つの紐付けられたデータを格納できる@型指定<ローマ字,ひらがな>
    String[] yayuyoArray;  //「〇ゃ・〇ゅ・〇ょ」が含まれている文字列全てを格納する配列
    String finalResult; //変換し終わった最終的な文字列を保存する変数


    //コンストラクタ部
    Hira2Roma01(String text){

        hiralines = new ArrayList<String>();  //ArrayListクラスをインスタンス化
        hira2romamap = new HashMap<String, String>();  //HashMapクラスをインスタンス化
        hira2romamap = setHira2RomaMap();  //作成したマップの対応表を保存する。
        yayuyoArray = new String[43];  //String配列をインスタンス化
        yayuyoArray = setYayuyoArray(); //作成した配列を保存する。
        roma2hiramap = new HashMap<String, String>();  //HashMapクラスをインスタンス化
        roma2hiramap = setRoma2HiraMap();  //作成したマップの対応表を保存する。


        /*
        hiralines = kanayoui("hiragana.txt");
        String adjust = ""; //adjustHiraメソッドに使うための変数

        for (String preP : hiralines) { //一時的にStringに変換し、adjustメソッドを使用できるようにする。
			adjust += preP;
		}
        System.out.println(adjust);
        */

        System.out.println("受け取った文字列:"+text);

        String adhira = adjustHira(text, hira2romamap); //adjustHiraメソッドでひらがなを調節する。

        System.out.println("調整したひらがな："+ adhira);

        //再びArrayListのhiralinesに戻す
        hiralines = new ArrayList<String>(); //再び初期化し
        hiralines.add(adhira); //listに調整した文字列を追加する

        System.out.println(hiralines);

        for ( String hira : hiralines ){  //ArrayListに保存された文字列を一要素ずつ取り出していく。取り出した要素は仮の変数hiraで記述される。
            //拡張for文：配列やListなどのコレクションのすべての要素に対して順番に処理を行うために用いる。
            String roma = transHtoR(hira,hira2romamap,yayuyoArray);  //transHtoRメソッドで変換したローマ字を保存する。
            System.out.println("元の文字列:" + hira);
            System.out.println("ローマ字に変換した文字列:" + roma);
            String reroma = reverse(roma);  //reverseメソッドで逆順にしたローマ字を保存する。
            System.out.println("ローマ字を逆順にした文字列:" + reroma);
            String rehira = transRtoH(reroma,roma2hiramap); //transRtoHメソッドで変換したひらがなを保存する。
            System.out.println("逆順ローマ字をひらがなに変換した文字列:" + rehira);
            finalResult = rehira; //最終的な結果を保存する。
        }
    }

    //ひらがなをローマ字に変換するメインのアルゴリズム
    public static String transHtoR(String line,HashMap<String, String> map,String[] yayuyo1F){ //引数(文字列,マップ,小文字判定用配列)
        String resultHtoR = ""; //変換した結果を保存する変数。
        String h1,r1,h2;
        boolean cheakResult; //判定した結果を保存する変数。
        line += " "; line += " "; //for文でエラーが出ないように文字列の長さを空白で調整する。
        for (int i=0; i<=line.length()-2; i++){ //引数の文字列の長さを取得しforループを回す。
            //Stringクラスのsubstringメソッドでその文字列から部分文字列を抜き出す。
            //substring(抜き出し開始位置,抜き出し終了位置)、ただし終了位置の文字は含まない。
            h1 = line.substring(i,i+1); //マップに対応させる用(1文字)
            h2 = line.substring(i,i+2); //「〇ゃ・〇ゅ・〇ょ」が含まれるか調べる用(2文字)
            //「〇ゃ・〇ゅ・〇ょ」が存在するか調べる。
            cheakResult = yayuyoCheak(h2,yayuyo1F);
            //「〇ゃ・〇ゅ・〇ょ」が存在するときの処理
            if(cheakResult){
                r1 = map.get(h2); //getメソッドは引数に「キー」を指定することで、そのキーの値を取得できる。
                if(r1 == null){ //もしmapに対応する値が存在しなかったら
                    resultHtoR += r1; //変換前のひらがなを結果的に出力する文字列に繋げる。
                }
                else{ //それ以外ならmapで取得した対応する値を結果的に出力する文字列に繋げる。
                    resultHtoR += r1;
                }
                i++; //2文字分処理したのでiを進める。
            }
            //「〇ゃ・〇ゅ・〇ょ」が存在しないときの処理
            else{
                r1 = map.get(h1); //getメソッドは引数に「キー」を指定することで、そのキーの値を取得できる。
                if(r1 == null){ //もしmapに対応する値が存在しなかったら
                    resultHtoR += h1; //変換前のひらがなを結果的に出力する文字列に繋げる。
                }
                else{ //それ以外ならmapで取得した対応する値を結果的に出力する文字列に繋げる。
                    resultHtoR += r1;
                }
            }
        }
        return resultHtoR; //変換した結果を返す。
    }

    //ひらがな→ローマ字返還した文字列を逆順にするメソッド
    public static String reverse(String roman){
        //StringBuilderクラスのreverse()メソッドを用いる。
        StringBuilder strb = new StringBuilder(roman);
        String tmp = strb.reverse().toString(); //元の文字列を逆順にする。
        return tmp; //逆順にした結果を返す。
    }

    //文字列の中に「〇ゃ・〇ゅ・〇ょ」のいずれかが含まれているか確認するメソッド
    public static boolean yayuyoCheak(String needck,String[] yayuyoB1){
        boolean B = false; //返り値用
        //「〇ゃ・〇ゅ・〇ょ」の全パターンが含まれている配列を用いて実際にチェックを行う。
        for(int m=0; m<42; m++){
            //Stringクラスのequalsメソッドで2つの文字列が一致しているかどうかを調べる。
            if(needck.equals(yayuyoB1[m])){ //もし「〇ゃ・〇ゅ・〇ょ」と一致したら
                B = true; //trueとしてループを抜ける。
                break;
            }
            else{
                B = false;
            }
        }
        return B; //返り値としてチェックの結果を返す。
    }


    //ローマ字をひらがなに変換するメインのアルゴリズム(キーボードでひらがなを入力するアルゴリズムを参考にした。)
    public static String transRtoH(String line,HashMap<String, String> map01){ //引数(文字列,マップ)
        String resultRtoH = ""; //変換した結果を保存する変数。
        String r1,h1;
        String stock = ""; //取得した文字をストックする変数
        String tmp = ""; //前回のループのストックを保存する変数
        boolean pb = false; //前回取得した文字が子音でストックに子音が存在している場合このフラグを立てる。
        int classifiedNumber; //取得した文字の分類結果を保存する変数
        boolean preSymbol = false; //前回取得した文字が記号だった場合このフラグを立てる。

        line += " "; //line += " "; //for文で範囲外エラーが出ないように文字列の長さを空白で調整する。

        for (int n=1; n<=line.length()-1; n++){ //引数の文字列の長さを取得しforループを回す。
            //Stringクラスのsubstringメソッドでその文字列から部分文字列を抜き出す。
            //substring(抜き出し開始位置,抜き出し終了位置)、ただし終了位置の文字は含まない。
            r1 = line.substring(n,n+1); //1文字分文字列から取得して

            System.out.println("今回取得したのは["+ r1 +"]です");

            tmp = stock; //更新前のストックを一時保存
            stock += r1; //取得した文字をストックして

            //取得した文字の分類を行う。
            classifiedNumber = classifyRoma(r1);

            System.out.println("classifiedNumber:"+classifiedNumber);

            //もし取得した文字が母音なら
            if(classifiedNumber == 0){
                h1 = map01.get(stock); //ストックしてあったローマ字文字列をマップに対応させてひらがなに変換して
                resultRtoH += h1; //変換後のひらがなを結果的に出力する文字列に繋げる。
                stock = ""; //文字をストックする変数は初期化しておく。
                //con = false; //取得した文字は母音なのでフラグを下げる。
                pb = false; //前回取得した文字が子音であるかどうかのフラグも下げる。
                preSymbol = false; //記号を取得しなかったのでフラグをさげる。
                System.out.println("これは母音です。");
            }

            //！！！子音の連続は「子音+y」しか許されない！！！

            //今回取得した文字が"y"以外の子音のとき
            else if(classifiedNumber == 2){
                if(pb == true){ //前回取得した文字が子音ならば
                    h1 = map01.get(tmp); //前回のループで取得した子音のみをひらがなに変換して
                    resultRtoH += h1; //変換後のひらがなを結果的に出力する文字列に繋げる。
                    stock = r1; //ストック変数には今回取得した文字のみを保存する。
                    //conはそのまま
                }
                //前回取得した文字が子音でない場合、特に処理はしない。
                System.out.println("今回取得した文字がy以外の子音でした");
                pb = true; //ストックに子音が入ったのでフラグを立てる。
                preSymbol = false; //記号を取得しなかったのでフラグをさげる。
            }
            //取得した文字が「y」のとき
            else if(classifiedNumber == 3){
                if(pb == true){ //もしストックに子音が連続していて
                    //前回のループでストックされていた子音が「r・m・h・n・t・s・k・b・p・d・j・g」のいずれかでないならば
                    if(!tmp.equals("r") || !tmp.equals("m") || !tmp.equals("h") || !tmp.equals("n") || !tmp.equals("t") || !tmp.equals("s") || !tmp.equals("k") || !tmp.equals("b") || !tmp.equals("p") || !tmp.equals("d") || !tmp.equals("j") || !tmp.equals("g")){
                        h1 = map01.get(tmp); //前回のループで取得した子音のみをひらがなに変換して
                        resultRtoH += h1; //変換後のひらがなを結果的に出力する文字列に繋げる。
                        stock = r1; //ストック変数には今回取得した文字のみを保存する。
                    }
                }
                //ストックに子音が存在しなかったら特に何もしない。
                pb = true; //ストックに子音が入ったことを示すフラグを立てる。
                preSymbol = false; //記号を取得しなかったのでフラグをさげる。
            }
            //取得した文字が記号または「N」(ん)のとき
            else if(classifiedNumber == 1){
                if(n == 1 || preSymbol == true){ //1文字目の初回ループもしくは前回所得した文字が記号ならば
                    h1 = map01.get(r1); //今回取得した文字を変換して
                    resultRtoH += h1; //変換後の結果を出力する文字列に繋げる。
                }
                else{ //2文字目以降のループならば
                    if(tmp != null && map01.get(tmp) != null){ //tmpが空っぽでなく、マップに対応するものが存在するときのみ
                        h1 = map01.get(tmp); //前回取得した文字をローマ字→ひらがな変換表で変換を行い
                        resultRtoH += h1; //変換後の結果を出力する文字列に繋げる。
                    }
                    h1 = map01.get(r1); //今回取得した文字も同様に変換して
                    resultRtoH += h1; //変換後の結果を出力する文字列に繋げる。
                }
                stock = ""; //ストック変数は初期化する。
                pb = false; //ストックは空っぽになるのでフラグは下げる。
                preSymbol = true; //記号を取得したのでフラグをあげる。
            }
            System.out.println("現在の状況  "+resultRtoH);
        }
        System.out.println(line + "がこうなりました");
        return resultRtoH; //変換した結果を返す。
    }



    //発音を奇麗にするために文字列を微調整するメソッド
    public static String adjustHira(String preHira,HashMap<String, String> map00){

        String resultH = ""; //調整した結果を保存する変数。
        String gh1 = ""; //取得した1文字目を保存する変数。
        String gh2 = ""; //取得した2文字目を保存する変数。
        String tmp1 = ""; String tmp2 = ""; String tmp3 = ""; //値を一時保存する変数

        HashMap<String, String> mapVowel = new HashMap<String, String>();  //母音用のマップを作製
        mapVowel.put("a","あ");
        mapVowel.put("i","い");
        mapVowel.put("u","う");
        mapVowel.put("e","え");
        mapVowel.put("o","お");

        preHira += " ";  //for文で範囲外エラーが出ないように文字列の長さを空白で調整する。

        for (int n=0; n<=preHira.length()-2; n++){ //引数の文字列の長さを取得しforループを回す。
            //Stringクラスのsubstringメソッドでその文字列から部分文字列を抜き出す。
            //substring(抜き出し開始位置,抜き出し終了位置)、ただし終了位置の文字は含まない。
            gh1 = preHira.substring(n,n+1); //1文字目を文字列から取得
            gh2 = preHira.substring(n+1,n+2); //2文字目を文字列から取得
            if(gh2.equals("ー")){ //もし二文字目が"ー"ならば
                tmp1 = map00.get(gh1); //所得した1文字目をローマ字に変換して
                tmp1 += " "; //範囲外エラーを回避するために長さを調整し
                tmp2 = tmp1.substring(tmp1.length()-2,tmp1.length()-1); //そのローマ字文字列の最後の記号＝母音を取得し
                tmp3 = mapVowel.get(tmp2); //それをひらがなの母音に直す。
                resultH += gh1; //結果として"ー"は直前の文字の母音に変換される。
                resultH += tmp3; //例："かー"→"かあ"
                n++; //二文字分処理したので進める。
            }
            else{
                resultH += gh1; //取得したひらがなをそのまま結果にくっつける。
            }
        }
        return resultH; //調整した結果を返す。
    }





    //取得したローマ字が母音か、「y」以外の子音か、「y」か、記号(Nも含む)かどうかを分類するメソッド
    public static int classifyRoma(String lineRoma){
        String sampleStr = lineRoma; //引数を保存する変数
        int classifyNum = 0; //4つの条件を保存する変数
        //もし文字が母音ならば
        if(sampleStr.equals("a") || sampleStr.equals("i") || sampleStr.equals("u") || sampleStr.equals("e") || sampleStr.equals("o")){
            classifyNum = 0; //変数に分類番号0を割り当てる。
        }
        //もし「y」以外の子音ならば
        else if(!sampleStr.equals("y")){
            //さらにそれが記号ならば
            if(sampleStr.equals(" ") || sampleStr.equals("　") || sampleStr.equals("_") || sampleStr.equals("!") || sampleStr.equals("！") || sampleStr.equals("?") || sampleStr.equals("？") || sampleStr.equals("N") || sampleStr.equals("ー")){
                classifyNum = 1; //変数に分類番号1を割り当てる。
            }
            //記号でなければ
            else{
                classifyNum = 2; //変数に分類番号2を割り当てる。
            }
        }
        //もし「y」ならば
        else if(sampleStr.equals("y")){
            classifyNum = 3; //変数に分類番号3を割り当てる。
        }
        return classifyNum; //分類した結果を返す。

    }


    //「〇ゃ・〇ゅ・〇ょ」が含まれている文字列を格納する配列を作成する。
    public static String[] setYayuyoArray(){
        //チェックすべき「〇ゃ・〇ゅ・〇ょ」が含まれている文字列を格納する配列
        String[] yayuyoArray0 = new String[43];
        yayuyoArray0[0] = "じゃ";
        yayuyoArray0[1] = "じゅ";
        yayuyoArray0[2] = "じょ";
        yayuyoArray0[3] = "ぢゃ";
        yayuyoArray0[4] = "ぢゅ";
        yayuyoArray0[5] = "ぢょ";
        yayuyoArray0[6] = "ぎゃ";
        yayuyoArray0[7] = "ぎゅ";
        yayuyoArray0[8] = "ぎょ";
        yayuyoArray0[9] = "びゃ";
        yayuyoArray0[10] = "びゅ";
        yayuyoArray0[11] = "びょ";
        yayuyoArray0[12] = "ぴゃ";
        yayuyoArray0[13] = "ぴゅ";
        yayuyoArray0[14] = "ぴょ";
        yayuyoArray0[15] = "りゃ";
        yayuyoArray0[16] = "りゅ";
        yayuyoArray0[17] = "りょ";
        yayuyoArray0[18] = "みゃ";
        yayuyoArray0[19] = "みゅ";
        yayuyoArray0[20] = "みょ";
        yayuyoArray0[21] = "ひゃ";
        yayuyoArray0[22] = "ひゅ";
        yayuyoArray0[23] = "ひょ";
        yayuyoArray0[24] = "にゃ";
        yayuyoArray0[25] = "にゅ";
        yayuyoArray0[26] = "にょ";
        yayuyoArray0[27] = "ちゃ";
        yayuyoArray0[28] = "ちゅ";
        yayuyoArray0[29] = "ちょ";
        yayuyoArray0[30] = "しゃ";
        yayuyoArray0[31] = "しゅ";
        yayuyoArray0[32] = "しょ";
        yayuyoArray0[33] = "きゃ";
        yayuyoArray0[34] = "きゅ";
        yayuyoArray0[35] = "きょ";
        yayuyoArray0[36] = "びぃ";
        yayuyoArray0[37] = "しぃ";
        yayuyoArray0[38] = "でぃ";
        yayuyoArray0[39] = "じぃ";
        yayuyoArray0[40] = "じぇ";
        yayuyoArray0[41] = "てぃ";
        yayuyoArray0[42] = "とぅ";
        return yayuyoArray0;
    }



    //ひらがなからローマ字に変換する対応表を作成する。
    public static HashMap<String, String> setHira2RomaMap(){  //返り値はHashMap
        HashMap<String, String> map0 = new HashMap<String, String>();  //新たにマップを宣言してインスタンス化する。
        //putメソッドは第1引数に「キー」、第2引数に「値」を指定することでデータをマップに追加できる。
        map0.put( "　", " " );
        map0.put( " ", " " );
        map0.put( "ぁ", "a" );
        map0.put( "あ", "a" );
        map0.put( "ぃ", "i" );
        map0.put( "い", "i" );
        map0.put( "ぅ", "u" );
        map0.put( "う", "u" );
        map0.put( "ぇ", "e" );
        map0.put( "え", "e" );
        map0.put( "ぉ", "o" );
        map0.put( "お", "o" );
        map0.put( "か", "ka" );
        map0.put( "が", "ga" );
        map0.put( "き", "ki" );
        map0.put( "ぎ", "gi" );
        map0.put( "く", "ku" );
        map0.put( "ぐ", "gu" );
        map0.put( "け", "ke" );
        map0.put( "げ", "ge" );
        map0.put( "こ", "ko" );
        map0.put( "ご", "go" );
        map0.put( "さ", "sa" );
        map0.put( "ざ", "za" );
        map0.put( "し", "si" );
        map0.put( "じ", "ji" );
        map0.put( "す", "su" );
        map0.put( "ず", "zu" );
        map0.put( "せ", "se" );
        map0.put( "ぜ", "ze" );
        map0.put( "そ", "so" );
        map0.put( "ぞ", "zo" );
        map0.put( "た", "ta" );
        map0.put( "だ", "da" );
        map0.put( "ち", "ti" );
        map0.put( "ぢ", "di" );
        map0.put( "っ", "_" );
        map0.put( "つ", "tu" );
        map0.put( "づ", "du" );
        map0.put( "て", "te" );
        map0.put( "で", "de" );
        map0.put( "と", "to" );
        map0.put( "ど", "do" );
        map0.put( "な", "na" );
        map0.put( "に", "ni" );
        map0.put( "ぬ", "nu" );
        map0.put( "ね", "ne" );
        map0.put( "の", "no" );
        map0.put( "は", "ha" );
        map0.put( "ば", "ba" );
        map0.put( "ひ", "hi" );
        map0.put( "び", "bi" );
        map0.put( "ふ", "fu" );
        map0.put( "ぶ", "bu" );
        map0.put( "へ", "he" );
        map0.put( "べ", "be" );
        map0.put( "ほ", "ho" );
        map0.put( "ぼ", "bo" );
        map0.put( "ぽ", "po" );
        map0.put( "ぱ", "pa" );
        map0.put( "ぴ", "pi" );
        map0.put( "ぷ", "pu" );
        map0.put( "ぺ", "pe" );
        map0.put( "ま", "ma" );
        map0.put( "み", "mi" );
        map0.put( "む", "mu" );
        map0.put( "め", "me" );
        map0.put( "も", "mo" );
        map0.put( "や", "ya" );
        map0.put( "ゆ", "yu" );
        map0.put( "よ", "yo" );
        map0.put( "ら", "ra" );
        map0.put( "り", "ri" );
        map0.put( "る", "ru" );
        map0.put( "れ", "re" );
        map0.put( "ろ", "ro" );
        map0.put( "ゎ", "^wa" );
        map0.put( "わ", "wa" );
        map0.put( "ゐ", "i" );
        map0.put( "ゑ", "e" );
        map0.put( "を", "wo" );
        map0.put( "ん", "n" );
        map0.put( "ゔ", "vu" );
        map0.put( "りゃ", "rya" );
        map0.put( "りゅ", "ryu" );
        map0.put( "りょ", "ryo" );
        map0.put( "みゃ", "mya" );
        map0.put( "みゅ", "myu" );
        map0.put( "みょ", "myo" );
        map0.put( "ひゃ", "hya" );
        map0.put( "ひゅ", "hyu" );
        map0.put( "ひょ", "hyo" );
        map0.put( "にゃ", "nya" );
        map0.put( "にゅ", "nyu" );
        map0.put( "にょ", "nyo" );
        map0.put( "ちゃ", "tya" );
        map0.put( "ちゅ", "tyu" );
        map0.put( "ちょ", "tyo" );
        map0.put( "しゃ", "sya" );
        map0.put( "しゅ", "syu" );
        map0.put( "しょ", "syo" );
        map0.put( "きゃ", "kya" );
        map0.put( "きゅ", "kyu" );
        map0.put( "きょ", "kyo" );
        map0.put( "びゃ", "bya" );
        map0.put( "びゅ", "byu" );
        map0.put( "びょ", "byo" );
        map0.put( "ぴゃ", "pya" );
        map0.put( "ぴゅ", "pyu" );
        map0.put( "ぴょ", "pyo" );
        map0.put( "ぢゃ", "dya" );
        map0.put( "ぢゅ", "dyu" );
        map0.put( "ぢょ", "dyo" );
        map0.put( "じゃ", "jya" );
        map0.put( "じゅ", "jyu" );
        map0.put( "じょ", "jyo" );
        map0.put( "ぎゃ", "gya" );
        map0.put( "ぎゅ", "gyu" );
        map0.put( "ぎょ", "gyo" );
        map0.put( "ー", "ー" );
        map0.put( "!", "!" );
        map0.put( "?", "?" );
        map0.put( "！", "！" );
        map0.put( "？", "？" );
        map0.put( "びぃ", "bi" );
        map0.put( "しぃ", "si" );
        map0.put( "でぃ", "di" );
        map0.put( "じぃ", "ji" );
        map0.put( "じぇ", "jie" );
        map0.put( "てぃ", "ti" );
        map0.put( "とぅ", "tu" );

        map0.put( "ゃ", "yo" );
        map0.put( "ゅ", "yu" );
        map0.put( "ょ", "yo" );


        return map0;
    }


    //ローマ字からひらがなに変換する対応表を作成する。
    public static HashMap<String, String> setRoma2HiraMap(){  //返り値はHashMap
        HashMap<String, String> map1 = new HashMap<String, String>();  //新たにマップを宣言してインスタンス化する。
        //putメソッドは第1引数に「キー」、第2引数に「値」を指定することでデータをマップに追加できる。
        map1.put( " ", " " );
        map1.put( "a", "あ" );
        map1.put( "i", "い" );
        map1.put( "u", "う" );
        map1.put( "e", "え" );
        map1.put( "o", "お" );
        map1.put( "ka", "か" );
        map1.put( "ga", "が" );
        map1.put( "ki", "き" );
        map1.put( "gi", "ぎ" );
        map1.put( "ku", "く" );
        map1.put( "gu", "ぐ" );
        map1.put( "ke", "け" );
        map1.put( "ge", "げ" );
        map1.put( "ko", "こ" );
        map1.put( "go", "ご" );
        map1.put( "sa", "さ" );
        map1.put( "za", "ざ" );
        map1.put( "si", "し" );
        map1.put( "ji", "じ" );
        map1.put( "su", "す" );
        map1.put( "zu", "ず" );
        map1.put( "se", "せ" );
        map1.put( "ze", "ぜ" );
        map1.put( "so", "そ" );
        map1.put( "zo", "ぞ" );
        map1.put( "ta", "た" );
        map1.put( "da", "だ" );
        map1.put( "ti", "ち" );
        map1.put( "di", "ぢ" );
        map1.put( "_", "っ" );
        map1.put( "tu", "つ" );
        map1.put( "du", "づ" );
        map1.put( "te", "て" );
        map1.put( "de", "で" );
        map1.put( "to", "と" );
        map1.put( "do", "ど" );
        map1.put( "na", "な" );
        map1.put( "ni", "に" );
        map1.put( "nu", "ぬ" );
        map1.put( "ne", "ね" );
        map1.put( "no", "の" );
        map1.put( "ha", "は" );
        map1.put( "ba", "ば" );
        map1.put( "hi", "ひ" );
        map1.put( "bi", "び" );
        map1.put( "fu", "ふ" );
        map1.put( "bu", "ぶ" );
        map1.put( "he", "へ" );
        map1.put( "be", "べ" );
        map1.put( "ho", "ほ" );
        map1.put( "bo", "ぼ" );
        map1.put( "po", "ぽ" );
        map1.put( "pa", "ぱ" );
        map1.put( "pi", "ぴ" );
        map1.put( "pu", "ぷ" );
        map1.put( "pe", "ぺ" );
        map1.put( "ma", "ま" );
        map1.put( "mi", "み" );
        map1.put( "mu", "む" );
        map1.put( "me", "め" );
        map1.put( "mo", "も" );
        map1.put( "ya", "や" );
        map1.put( "yu", "ゆ" );
        map1.put( "yo", "よ" );
        map1.put( "ra", "ら" );
        map1.put( "ri", "り" );
        map1.put( "ru", "る" );
        map1.put( "re", "れ" );
        map1.put( "ro", "ろ" );
        map1.put( "wa", "わ" );
        map1.put( "wo", "を" );
        map1.put( "wu", "う");
        map1.put( "wi", "うぃ" );
        map1.put( "vu", "ゔ" );
        map1.put( "rya", "りゃ" );
        map1.put( "ryu", "りゅ" );
        map1.put( "ryo", "りょ" );
        map1.put( "mya", "みゃ" );
        map1.put( "myu", "みゅ" );
        map1.put( "myo", "みょ" );
        map1.put( "hya", "ひゃ" );
        map1.put( "hyu", "ひゅ" );
        map1.put( "hyo", "ひょ" );
        map1.put( "nya", "にゃ" );
        map1.put( "nyu", "にゅ" );
        map1.put( "nyo", "にょ" );
        map1.put( "tya", "ちゃ" );
        map1.put( "tyu", "ちゅ" );
        map1.put( "tyo", "ちょ" );
        map1.put( "sya", "しゃ" );
        map1.put( "syu", "しゅ" );
        map1.put( "syo", "しょ" );
        map1.put( "kya", "きゃ" );
        map1.put( "kyu", "きゅ" );
        map1.put( "kyo", "きょ" );
        map1.put( "bya", "びゃ" );
        map1.put( "byu", "びゅ" );
        map1.put( "byo", "びょ" );
        map1.put( "pya", "ぴゃ" );
        map1.put( "pyu", "ぴゅ" );
        map1.put( "pyo", "ぴょ" );
        map1.put( "dya", "ぢゃ" );
        map1.put( "dyu", "ぢゅ" );
        map1.put( "dyo", "ぢょ" );
        map1.put( "jya", "じゃ" );
        map1.put( "jyu", "じゅ" );
        map1.put( "ju", "じゅ" );
        map1.put( "jyo", "じょ" );
        map1.put( "gya", "ぎゃ" );
        map1.put( "gyu", "ぎゅ" );
        map1.put( "gyo", "ぎょ" );
        map1.put( "fi", "ふぃ" );
        map1.put( "ー", "ー" );
        map1.put( "!", "!" );
        map1.put( "?", "?" );
        map1.put( "！", "！" );
        map1.put( "？", "？" );
        map1.put( " ", " ");
        map1.put( "　", "　");
        //子音のみの場合は最も近い発音のものを採用する。
        map1.put( "n", "ん" );
        map1.put( "m", "む" );
        map1.put( "b", "ぶ" );
        map1.put( "v", "ゔ" );
        map1.put( "c", "ち" );
        map1.put( "x", "く" );
        map1.put( "z", "ず" );
        map1.put( "s", "す" );
        map1.put( "d", "どぅ" );
        map1.put( "f", "ふ" );
        map1.put( "g", "ぐ" );
        map1.put( "h", "ふ" );
        map1.put( "j", "じ" );
        map1.put( "k", "く" );
        map1.put( "l", "る" );
        map1.put( "p", "ぷ" );
        map1.put( "y", "ぃ" );
        map1.put( "t", "とぅ" );
        map1.put( "r", "る" );
        map1.put( "w", "ぅ" );
        map1.put( "q", "く" );

        //01/18修正部分
        map1.put( "yi", "ぃ" );
        map1.put( "zi", "じ" );
        map1.put( "hu", "ふ" );


        return map1;
    }
}




