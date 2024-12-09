package learn.foraging.report;

import learn.foraging.models.Category;
import learn.foraging.models.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryResult {

    private HashMap<Category, BigDecimal> payload;
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

    public HashMap<Category, BigDecimal> getPayload() {
        return payload;
    }

    public void setPayload(HashMap<Category, BigDecimal> payload) {
        this.payload = payload;
    }
}
