package controller;

import model.LineObj;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;
//los numeros estan separados por comas no por puntos
// el tema esta en cuando no hay "" entra en el atributo directo , si hay "" leemos desde la 1 hasta encontrar la 2  y lo metenmos en el atributo
public class Logic {
    List<LineObj> outPutData;
    public void readFile(){
        Path path = Paths.get("peliculas.csv");

        Function<String, LineObj> convertidor = p -> {
            //String[] a = p.split("\"");
          //  String[] a = p.split(",");
            String [] a = new String[]{p.replace(",", ".")};

            for (int i = 0; i < a.length; i++) {
                System.out.print(a[i]);

            }




            //MIRAR ESTO PORQUE VAMOS......
   //         SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd hh-mm-ss", Locale.ENGLISH);
//            String c = a[4];
//            try {
//                Date date = new SimpleDateFormat("yyyy-MMM-dd hh-mm-ss").parse(a[4]);
//                return new LineObj(Integer.parseInt(a[0]), a[1], Integer.parseInt(a[2]), Integer.parseInt(a[3]), date, a[5]);
//
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }


            return null;
        };

        try {
            if (fileExist(path.toFile())){
                outPutData=Files.lines(path).skip(1).map(convertidor).toList();
                System.out.println(outPutData);


            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }




    }

    private static boolean fileExist(File file) {
        boolean exist;
        exist = Files.exists(Paths.get(file.toURI()));
        return exist;
    }


}


//          return new LineObj(Integer.parseInt(a[0]),a[1],Integer.parseInt(a[2]),Integer.parseInt(a[3]),formatter.parse(a[4]),a[5]);

