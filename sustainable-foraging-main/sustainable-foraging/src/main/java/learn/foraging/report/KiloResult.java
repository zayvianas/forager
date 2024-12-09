package learn.foraging.report;

import learn.foraging.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KiloResult {

    private HashMap<Item, Double> payload;
    private ArrayList<String> messages = new ArrayList<>();

    public boolean isSuccess() {
        return messages.size() == 0;
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(messages);
    }

    public void addErrorMessage(String message) {
        messages.add(message);
    }

    public HashMap<Item, Double> getPayload() {
        return payload;
    }

    public void setPayload(HashMap<Item, Double> payload) {
        this.payload = payload;
    }
}
