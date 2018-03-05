package com.sanjinbest.burro.core.mock;

import javax.sound.midi.Soundbank;
import java.io.PrintStream;
import java.util.Random;
import java.util.logging.Level;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2018/1/15 下午5:01
 */
public class DP {

//    static int m = 0;
//
//    public int getDP(int n){
//        m++;
//        if(n == 1)return 1;
//        if(n == 2)return 2;
//        return getDP(n - 1) + getDP(n - 2);
//    }
//
//    public int getDP1(int n){
//        m++;
//        if(n == 1)return 1;
//        if(n == 2)return 2;
//
//        int a = 1;
//        int b = 2;
//        int temp = 0;
//        int sum = 0;
//
//        for(int i = 3;i <= n;i++){
//            sum = a + b;
//            a = b;
//            b = sum;
//        }
//        return sum;
//    }

    /***********************************************************************/

    public void bubbleSort(int[] arr){
        int mov = 0;
        int comp = 0;
        int tmp = 0;
        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < arr.length - i - 1; ++j) {
                comp++;
                if (arr[j] > arr[j + 1]) {
                    mov++;
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }

        System.out.println(mov+"  "+comp);
    }

    public void bubbleSort1(int[] arr){
        int mov = 0;
        int comp = 0;
        int tmp = 0;
        boolean swap = true;
        for (int i = 0; i < arr.length; ++i) {
            swap = true;
            for (int j = 0; j < arr.length - i - 1; ++j) {
                comp++;
                if (arr[j] > arr[j + 1]) {
                    swap = false;
                    mov++;
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
            if(swap == true){
                break;
            }
        }

        System.out.println(mov+"  "+comp);
    }

    public void bubbleSort2(int[] arr){
        int mov = 0;
        int comp = 0;
        int tmp = 0;
        int flag = arr.length;
        int m = 0;
        for (int i = 0; i < flag; ++i) {
            m = flag;
            flag = 0;
            for (int j = 0; j < m - 1; ++j) {
                comp++;
                if (arr[j] > arr[j + 1]) {
                    mov++;
                    flag = j;
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }

        System.out.println(mov+"  "+comp);
    }

    /***********************************************************************/

    //插入排序
    void insertSort(int arr[]) {
        for(int i= 1; i<arr.length; i++){
            if(arr[i] < arr[i-1]){//如果小于前一位，开始定位插入位置
                int j = i-1; //标记开始后移的位置
                int temp = arr[i];  //临时存储待插入数据
                while(j > -1 && temp < arr[j]){ //遍历定位位置同时向后移动
                    arr[j+1] = arr[j];
                    j--;
                }
                arr[j+1] = temp;
            }
        }

        for(int a : arr){
            System.out.println(a);
        }
    }

    /***********************************************************************/

    public void sheelSort(int arr[]){
        int dk = arr.length/2;
        while( dk >= 1  ){
            shellSort(arr, dk);
            dk = dk/2;
        }
    }

    //希尔排序
    private void shellSort(int arr[],int k){
        for(int i = k;i<arr.length;i++){
            if(arr[i] < arr[i - k]){
                int j = i - k;
                int temp = arr[i];
                while(j > -1 && temp < arr[j]){
                    arr[j + k] = arr[j];
                    j -= k;
                }
                arr[j + k] = temp;
            }
        }
        for(int a : arr){
            System.out.print(a+"  ");
        }
        System.out.println();
    }

    /***********************************************************************/

    //简单选择排序
    public void simpleSelectionSort(int[] arr){
        int m = 0;  //启始插入位置
        int n = 0;  //最小数值位置
        while(m < arr.length){
            n = m;
            for(int i = n+1;i < arr.length;i++){//找出n
                if(arr[i] < arr[n])
                    n = i;
            }
            int temp = arr[m];
            arr[m] = arr[n];
            arr[n] = temp;
            m++;
        }
        for(int a : arr){
            System.out.print(a+"  ");
        }
    }

    /***********************************************************************/

    //快速排序
    public void quickSort(int[] arr,int low,int high){
        if(low < high){
            int loc = partition(arr,low,high);
            quickSort(arr,low,loc - 1);
            quickSort(arr,loc + 1,high);
        }
    }

    //将数组拆分,返回拆分的节点下标
    private int partition(int[] arr,int low,int high){
        int standard = arr[low];
        while(low < high){
            //从后向前
            while(low < high && arr[high] >= standard)--high;
            arr[low] = arr[high];
            //从前向后
            while(low < high && arr[low] <= standard)++low;
            arr[high] = arr[low];
        }
        arr[high] = standard;

        return low;
    }

    /***********************************************************************/

    //递归进行拆分
    public void mergeSort(int[] arr,int left,int right){
        if(left >= right){
            return;
        }

        int center = (right + left)/2;
        mergeSort(arr,left,center);
        mergeSort(arr,center + 1,right);
        merge(arr,left,right,center);
    }

    //合并排序
    private void merge(int[] arr,int left,int right,int center){
        int[] tmpArr = new int[right - left + 1]; //存储排序结果
        int tmpIndex = 0;

        int start1 = left;
        int start2 = center + 1;

        while(start1 <= center && start2 <= right){
            if(arr[start1] < arr[start2]){
                tmpArr[tmpIndex++] = arr[start1++];
            }else{
                tmpArr[tmpIndex++] = arr[start2++];
            }
        }

        while(start1 <= center){
            tmpArr[tmpIndex++] = arr[start1++];
        }
        while(start2 <= right){
            tmpArr[tmpIndex++] = arr[start2++];
        }

        tmpIndex = 0;
        while(left <= right){
            arr[left++] = tmpArr[tmpIndex++];
        }
    }

    private void print(int[] arr){
        for(int a : arr){
            System.out.print(a+" ");
        }
        System.out.println();
    }


    public static void main(String[] args) {
        int[] arr = new int[100];
        Random random = new Random();
        for(int i=0;i<100;i++){
            arr[i] = random.nextInt(100);
        }
        DP dp = new DP();
        dp.print(arr);

        dp.bubbleSort(arr);
        dp.bubbleSort1(arr);
        dp.bubbleSort2(arr);

        dp.print(arr);
//        dp.mergeSort(arr,0,arr.length - 1);
//        dp.print(arr);
//        int dk = arr.length/2;
//        while( dk >= 1  ){
//            dp.shellInsertSort(arr, dk);
//            dk = dk/2;
//        }

//        dp.quickSort(arr,0,arr.length - 1);
    }
}
