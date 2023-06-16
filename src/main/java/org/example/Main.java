package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

// Interfejs do obsługi zapisu i odczytu danych
interface ConversionHistory {
    void saveConversion(String conversion);
    List<String> loadConversions();

}

// Klasa do obsługi konwersji jednostek wagi
class WeightConverter implements ConversionHistory { // implementuje interfejs, który definiuje metody do obsługi zapisu i odczytu historii konwersji
    private List<String> conversionHistory; // przechowuje historię w liście 'conversionHistory'

    public WeightConverter() {
        conversionHistory = new ArrayList<>();
    }

    public double convertKilogramsToGrams(double kilograms) { // wszystkie metody od tego w dół służą jdo przeliczania wartości między różnymi jednostkami wagi.
        return kilograms * 1000;
    }

    public double convertKilogramsToDekagrams(double kilograms) {
        return kilograms * 100;
    }

    public double convertGramsToKilograms(double grams) {
        return grams / 1000;
    }

    public double convertGramsToDekagrams(double grams) {
        return grams * 0.01;
    }

    public double convertDekagramsToKilograms(double dekagrams) {
        return dekagrams * 0.001;
    }

    public double convertDekagramsToGrams(double dekagrams) {
        return dekagrams * 10;
    }


    public void saveConversion(String conversion) { // ta metoda służy do zapisywania konwersji do pliku
        conversionHistory.add(conversion);
        try {
            FileWriter fileWriter = new FileWriter("conversions.txt", true); // tworzymy obiekt filewriter, dzięki któremu tworzymy plik tekstowy i spradzamy czy istnieje.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); // tworzymy obiekt bufferedwriter, który zapisuje historie konwersji do pliku
            bufferedWriter.write(conversion);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) { // jeśli wystąpi błąd to dostaniemy o tym informację
            e.printStackTrace();
        }
    }


    public List<String> loadConversions() { // ta metoda służy do zczytywania historii konwersji z pliku
        List<String> loadedConversions = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("conversions.txt"); // tworzymy obiekt filereader, gdzie w konstruktorze jako parametr podajemy nazwe stworzonego wcześniej pliku tekstowego.
            BufferedReader bufferedReader = new BufferedReader(fileReader); // tworzymy obiekt bufferedreader, który służy do zczytywania linii z pliku tekstowego.
            String line;
            while ((line = bufferedReader.readLine()) != null) { //  w pętli while sprawdzamy każdą linie z pliku tekstowego, która jest przypisywana do zmiennej line, a później ta zmienna jest dodawana do metody, która służy do wyświetlania tych wszystkich linii.
                loadedConversions.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) { // jeśli wystąpi błąd to dostaniemy o tym informację
            e.printStackTrace();
        }
        return loadedConversions;
    }
}

// Klasa interfejsu okienkowego
class WeightConverterGUI extends JFrame { // ta klasa jest klasą interfejsu użytkownika, która dziedziczy po klasie Jframe
    private final WeightConverter weightConverter; // pobieramy klase która posiada metody do przetwarzania wag

    private final JTextField inputField; // pole tekstowe, do którego będziemy wpisywać wartości
    private JButton gramsButton; // przycisk do wyboru rodzaju wagi: gramy
    private JButton dagsButton; // przycisk do wyboru rodzaju wagi: dekagramy
    private JButton kgsButton; // przycisk do wyboru rodzaju wagi: kilogramy
    private JButton historyButton; // przycisk do sprawdzenia historii konwersji
    private JTextArea outputArea; // obszar tekstowy, w którym będą pokazywać się wyniki konwersji

    public WeightConverterGUI() {
        weightConverter = new WeightConverter();

        // Utworzenie interfejsu użytkownika
        setTitle("Konwerter wag");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 300);
        setLayout(new FlowLayout());

        inputField = new JTextField(10);
        gramsButton = new JButton("Gramy");
        dagsButton = new JButton("Dekagramy");
        kgsButton = new JButton("Kilogramy");
        historyButton = new JButton("Historia");
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);

        gramsButton.addActionListener(new GramsButtonListener());
        dagsButton.addActionListener(new DagsButtonListener());
        kgsButton.addActionListener(new KgsButtonListener());
        historyButton.addActionListener(new HistoryButtonListener());

        add(new JLabel("Wprowadź wartość: "));
        add(dagsButton);
        add(inputField);
        add(gramsButton);
        add(kgsButton);
        add(historyButton);
        add(new JScrollPane(outputArea));

        setVisible(true);
    }
    private class HistoryButtonListener implements ActionListener { // tworzenie obsługi zdarzenia historii

        public void actionPerformed(ActionEvent e) {
            List<String> conversions = weightConverter.loadConversions(); //wykonuje się metoda zczytywania historii z pliku tekstowego
            if (conversions.isEmpty()) { // sprawdzanie czy plik nie jest pusty
                JOptionPane.showMessageDialog(WeightConverterGUI.this, "Brak historii konwersji.", // jeśli jest pusty to wyświetla wiadomość o tym
                        "Historia Konwersji", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String history = String.join("\n", conversions); // w drugim przypadku jeśli coś jest w pliku tekstowym to wyświetla jej zawartość za pomocą "join", ponieważ jest on w tablicy
                JOptionPane.showMessageDialog(WeightConverterGUI.this, history, "Historia Konwersji",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    private class GramsButtonListener implements ActionListener { // tworzenie obsługi zdarzenia dla kategorii gramy

        public void actionPerformed(ActionEvent e) {
            String inputValue = inputField.getText(); // zbieranie wartości z pola tekstowego
            double input = Double.parseDouble(inputValue); // wprowadzanie tej wartości do zmiennej input

            double kilograms = weightConverter.convertGramsToKilograms(input); // metoda do zmiany gramów na kilogramy
            double dekagrams = weightConverter.convertGramsToDekagrams(input); // metoda do zmiany gramów na dekagramy

            String conversion = inputValue + " g = " + kilograms + " kg i " + dekagrams + " dag"; // string, który ukazuje sie w obszarze tekstowym jako wynik działania
            weightConverter.saveConversion(conversion); // zapisywanie konwersji do pliku tekstowego za pomoca metody saveConversion

            outputArea.append(conversion + "\n"); // tworzenie nowej linii w obszarze tekstowym dla nowego wyniku

            inputField.setText(""); // usuwanie wartości z pola tekstowego dla komfortu użytkownika
        }
    }
    private class DagsButtonListener implements ActionListener { // tworzenie obsługi zdarzenia dla kategorii dekagramy

        public void actionPerformed(ActionEvent e) {
            String inputValue = inputField.getText(); // zbieranie wartości z pola tekstowego
            double input = Double.parseDouble(inputValue); // wprowadzanie tej wartości do zmiennej input

            double kilograms = weightConverter.convertDekagramsToKilograms(input); // metoda do zmiany dekagramów na kilogramy
            double grams = weightConverter.convertDekagramsToGrams(input); // metoda do zmiany dekagramów na gramy

            String conversion = inputValue + " dag = " + grams + " g i " + kilograms + " kg"; // string, który ukazuje sie w obszarze tekstowym jako wynik działania
            weightConverter.saveConversion(conversion); // zapisywanie konwersji do pliku tekstowego za pomoca metody saveConversion

            outputArea.append(conversion + "\n"); // tworzenie nowej linii w obszarze tekstowym dla nowego wyniku

            inputField.setText(""); // usuwanie wartości z pola tekstowego dla komfortu użytkownika
        }
    }
    private class KgsButtonListener implements ActionListener { // tworzenie obsługi zdarzenia dla kategorii kilogramy

        public void actionPerformed(ActionEvent e) {
            String inputValue = inputField.getText(); // zbieranie wartości z pola tekstowego
            double input = Double.parseDouble(inputValue); // wprowadzanie tej wartości do zmiennej input

            double grams = weightConverter.convertKilogramsToGrams(input); // metoda do zmiany kilogramów na gramy
            double dekagrams = weightConverter.convertKilogramsToDekagrams(input); // metoda do zmiany kilogramów na dekagramy

            String conversion = inputValue + " kg = " + grams + " g i " + dekagrams + " dag"; // string, który ukazuje sie w obszarze tekstowym jako wynik działania
            weightConverter.saveConversion(conversion); // zapisywanie konwersji do pliku tekstowego za pomoca metody saveConversion

            outputArea.append(conversion + "\n"); // tworzenie nowej linii w obszarze tekstowym dla nowego wyniku

            inputField.setText(""); // usuwanie wartości z pola tekstowego dla komfortu użytkownika
        }
    }
}

public class Main { // służy do uruchamiania aplikacji, tworząc instancje klasy WeightConverterGUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new WeightConverterGUI();
            }
        });
    }
}
