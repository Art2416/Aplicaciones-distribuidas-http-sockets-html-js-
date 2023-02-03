package escuelaing.edu.co.arep;

import java.net.*;
import java.util.HashMap;
import org.json.*;
import java.io.*;


/**
 * Crea un servidor web
 */
public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while(running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine, nombrePelicula="";


            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("info?title=")){
                    String[] array = inputLine.split("title=");
                    nombrePelicula = (array[1].split("HTTP")[0]).replace(" ", "");
                }
                if (!in.ready()) {
                    break;
                }
            }
            if(!nombrePelicula.equals("")){
                String info = Conection.busqueda(nombrePelicula, "http://www.omdbapi.com/?t=" + nombrePelicula + "&apikey=62c22013");
                outputLine ="HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<br>" + "<table border=\" 1 \"> \n " + organizacion(info)+ "    </table>";
            }else {
                outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + content();
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }




    /**
     * crea el contenido
     * @return contenido de la pagina
     */
    private static String content(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Info</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Información de la película a buscar</h1>\n" +
                "<form action=\"/search\">\n" +
                "    <label for=\"name\">Inserte nombre:</label><br>\n" +
                "    <input type=\"text\" id=\"name\" name=\"name\" value=\"Guardians of the Galaxy Vol. 2\"><br><br>\n" +
                "    <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "</form>\n" +
                "<div id=\"capture\"></div>\n" +
                "\n" +
                "<script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"capture\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/info?title=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * organiza la informacion
     * @param inf hace visible el contenido de manera organizada
     * @return informacion de la busqueda
     */
    private static String organizacion(String inf){

        HashMap<String,String> informacion = new HashMap<String, String>();
        HashMap<String,String> alma = new HashMap<String, String>();

        String organizar = "Información de la película:" + "<br>" + "<tr> \n";
        JSONArray jsonArray = new JSONArray(inf);

        for (int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);
            for (String key: object.keySet()) {
                String k;
                k = key.toString();
                informacion.put(key.toString(), object.get(key).toString());
                //System.out.println(k);
                //System.out.println(jsonArray.getJSONObject(i));
            }

        }
        //System.out.println(dict.keySet());


        for (String keys: informacion.keySet()){
            String value = informacion.get(keys);
            alma.put(keys, value);
            organizar += "<br>" + keys + ": " + value + "<br>";
            organizar += "<tr> \n";
        }
        return organizar;
    }
}