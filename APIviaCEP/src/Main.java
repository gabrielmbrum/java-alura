import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        char answer = 'y';
        String json, url, cep;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<CEPInfos> allInfos = new ArrayList<>();

        while (answer == 'y') {

            do {
                System.out.print("Digite o CEP (sem espaços ou traços): ");
                cep = sc.nextLine();
                cep = cep.replace("-", "");
                cep = cep.replace(".", "");
                cep = cep.replace(" ", "");
            } while (cep.length() < 8);

            url = "https://viacep.com.br/ws/" + cep + "/json/";

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                json = response.body();
                System.out.println(json);

                if (json.contains("erro") && json.contains("true"))
                    throw new CEPNonExistentException("!!! CEP INEXISTENTE !!!");

                CEPInfos infos = gson.fromJson(json, CEPInfos.class);
                System.out.println(infos);

                allInfos.add(infos);
            } catch (CEPNonExistentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro indefinido!");
                System.out.println(e.getMessage());
            }

            System.out.print("Do you want to keep searching? <y/n>\nR: ");
            answer = sc.nextLine().charAt(0);
        }

        if (!allInfos.isEmpty()) {
            File file = new File("C:\\java\\projects\\alura\\APIviaCEP\\cepinfos.json");
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(allInfos));
            writer.close();
        }
    }
}