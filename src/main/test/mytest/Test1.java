package mytest;

import com.alibaba.druid.sql.visitor.functions.Char;
import org.junit.Test;

import java.util.*;

public class Test1 {
    public int m=0;
    public void move(char A,int n,char C){
        m++;
        System.out.println("移动次数:"+m+"  "+n+" :"+A+"-->"+C);
    }

    public void Hanoi(int n,char A,char B,char C){
        if(n==1){
            move(A,1,C);
        }else {
            Hanoi(n-1,A,C,B);
            move(A,n,C);
            Hanoi(n-1,B,A,C);
        }
    }


    @Test
    public void test(){
        String s = "aab";
        int length = s.length(); //新建数组的长度
        if(length==1){
            System.out.println("no");
        }
        int uni[] = new int[26];
        int max_length = 0;
        for(char c:s.toCharArray()){
            if(max_length< ++uni[c-'a']) max_length = uni[c-'a'];//把字符映射到数组的下标，同时存放字符出现次数，还记录了最大的重复数
        }
        if(max_length>(length+1)/2){
            System.out.println("no");
        }
        int even=0,odd=1;
        char arr[] = new char[length];
        for(int i=0 ;i<26;i++){ //* 因为是用下标来标记的字符
            while (uni[i]>0&&odd<length&&uni[i]<(length/2+1)){ //* 循环把 其余值 放到奇数位置 * 每次比较字符频率是否剩下值的频率
                arr[odd]=(char) (i+'a');
                uni[i]--;
                odd+=2;
            }
            while (uni[i]>0){ //捡剩下的
                arr[even]=(char)(i+'a');
                uni[i]--;
                even+=2;
            }
        }
        System.out.println(new String(arr));



    }
    @Test
    public void fizzbuzz(){
        int n=15;
        ArrayList<String> arr = new ArrayList<>();
        for(int i=1;i<=n;i++){

           if(i%3==0&&i%5==0){
               arr.add("FizzBuzz");
           }else if(i%5==0){
               arr.add("Buzz");
           }else if(i%3==0){
               arr.add("Fizz");
           }else {
               arr.add(i+"");
           }

        }
        System.out.println(arr.toString());
    }
    @Test
    public void findFistIndex(){

    }

    static long fibo(int N){
        if(N==0||N==1) return N;
        int a=0,b=1,res=0;
        for(int i=2;i<N;i++){
            res = a+b;
            a = b;
            b = res;
        }
        return res;
    }
}
