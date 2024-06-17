package praktikosDarbas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import praktikosDarbas.entity.MeteoObject;
import praktikosDarbas.repository.MeteoRepository;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MeteoConection {

    @Autowired
    private MeteoRepository rep;

    public MeteoConection() {
        super();
    }

    @Scheduled(fixedRate = 300000)
    public void saveData() {
        String url = "http://170.7.1.16/AADI%20AaDispDbWebservice/AaDispDb.asmx?op=getLatestData_Using_DataTypeIds_and_Hours";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\r\n" +
                "  <soap12:Body>\r\n" +
                "    <getLatestData_Using_DataTypeIds_and_Hours xmlns=\"http://www.aadi.no/\">\r\n" +
                "      <data_type_ids>\r\n" +
                "        <string>5</string>\r\n" +
                "        <string>1</string>\r\n" +
                "        <string>6</string>\r\n" +
                "        <string>2</string>\r\n" +
                "        <string>3</string>\r\n" +
                "        <string>4</string>\r\n" +
                "        <string>7</string>\r\n" +
                "      </data_type_ids>\r\n" +
                "      <sBackHours>0</sBackHours>\r\n" +
                "      <sMaxRowCount>7</sMaxRowCount>\r\n" +
                "    </getLatestData_Using_DataTypeIds_and_Hours>\r\n" +
                "  </soap12:Body>\r\n" +
                "</soap12:Envelope>";

        HttpURLConnection con = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(xml);
                wr.flush();
            }

            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response.toString())));

            NodeList errNodes = doc.getElementsByTagName("ArrayOfString");
            List<Double> data = new ArrayList<>();
            String currDate = "s1";

            if (errNodes.getLength() > 0) {
                for (int i = 1; i < errNodes.getLength(); i++) {
                    NodeList first = (NodeList) errNodes.item(i);
                    Element err = (Element) first.item(5);
                    if (data.size() <= 0) {
                        Element elementDate = (Element) first.item(0);
                        currDate = elementDate.getTextContent();
                    }
                    data.add(Double.parseDouble(err.getTextContent()));
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date formatedDate = dateFormat.parse(currDate);

                MeteoObject collectedData = new MeteoObject(formatedDate, data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6));
                rep.save(collectedData);
            } else {
                // Log the response or handle the case where no data is returned
                System.out.println("No data received.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Use a logging framework in production
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
