package vttp.batch5.ssf.noticeboard.models;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Notice {

    @NotEmpty(message = "Title is required.")
    @Size(min = 3, max = 128, message = "Title must be between 3 and 128 characters.")
    private String title;

    @NotEmpty(message = "Poster email is required.")
    @Email(message = "Must be a valid email")
    private String poster;

    @NotNull(message = "Date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Date must be in the future")
    private Date postDate;

    @NotEmpty(message = "Category is required")
    @Size(min = 1, message = "At least one category is required")
    private List<String> categories;

    @NotEmpty(message = "Content of the notice is required.")
    private String text;

    public Notice() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return title + "," + poster + "," + postDate + "," + categories
                + "," + text;
    }

    public JsonObject toJson() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (String category : this.categories) {
            arrBuilder.add(category);
        }

        return Json.createObjectBuilder()
                .add("title", this.title)
                .add("poster", this.poster)
                .add("postDate", this.postDate.getTime())
                .add("categories", arrBuilder.build())
                .add("text", this.text)
                .build();
    }

    public static Notice toNotice(String json) {
        Notice notice = new Notice();

        // Convert the string to JSON
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject jsonObj = reader.readObject();

        notice.setTitle(jsonObj.getString("title"));
        notice.setPoster(jsonObj.getString("poster"));
        notice.setPostDate(new Date(jsonObj.getJsonNumber("postDate").longValue()));

        JsonArray categoryData = jsonObj.getJsonArray("categories");
        List<String> categories = new ArrayList<>();
        if (categoryData != null) {
            for (JsonValue category : categoryData) {
                categories.add(category.asJsonObject().getString("categories"));
            }
        }
        notice.setCategories(categories);

        return notice;
    }
}
