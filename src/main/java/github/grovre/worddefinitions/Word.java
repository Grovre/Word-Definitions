package github.grovre.worddefinitions;

import com.google.gson.Gson;
import kong.unirest.Unirest;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@lombok.NoArgsConstructor
@lombok.Data
@CommonsLog
public class Word {

    @com.fasterxml.jackson.annotation.JsonProperty("word")
    private String word;
    @com.fasterxml.jackson.annotation.JsonProperty("phonetics")
    private List<PhoneticsDTO> phonetics;
    @com.fasterxml.jackson.annotation.JsonProperty("meanings")
    private List<MeaningsDTO> meanings;
    @com.fasterxml.jackson.annotation.JsonProperty("license")
    private LicenseDTO license;
    @com.fasterxml.jackson.annotation.JsonProperty("sourceUrls")
    private List<String> sourceUrls;

    public static Word of(String word) {
        StringBuilder builder = new StringBuilder(Unirest.get("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                .asJson().getBody().toString());
        builder.deleteCharAt(0);
        builder.deleteCharAt(builder.length()-1);
        String jsonIntake = builder.toString();

        if(jsonIntake.contains("No Definitions Found")) {
            log.info(word + " is not recognized as a word.");
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonIntake, Word.class);
    }

    public List<String> getDefinitions() {
        List<String> definitions = new ArrayList<>();
        for(MeaningsDTO meaning : meanings) {
            for(MeaningsDTO.DefinitionsDTO defs : meaning.getDefinitions()) {
                String definition = defs.getDefinition();
                definitions.add(definition);
            }
        }
        return definitions;
    }

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class LicenseDTO {
        @com.fasterxml.jackson.annotation.JsonProperty("name")
        private String name;
        @com.fasterxml.jackson.annotation.JsonProperty("url")
        private String url;
    }

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class PhoneticsDTO {
        @com.fasterxml.jackson.annotation.JsonProperty("audio")
        private String audio;
        @com.fasterxml.jackson.annotation.JsonProperty("sourceUrl")
        private String sourceUrl;
        @com.fasterxml.jackson.annotation.JsonProperty("license")
        private PhoneticsDTO.LicenseDTO license;
        @com.fasterxml.jackson.annotation.JsonProperty("text")
        private String text;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class LicenseDTO {
            @com.fasterxml.jackson.annotation.JsonProperty("name")
            private String name;
            @com.fasterxml.jackson.annotation.JsonProperty("url")
            private String url;
        }
    }

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class MeaningsDTO {
        @com.fasterxml.jackson.annotation.JsonProperty("partOfSpeech")
        private String partOfSpeech;
        @com.fasterxml.jackson.annotation.JsonProperty("definitions")
        private List<DefinitionsDTO> definitions;
        @com.fasterxml.jackson.annotation.JsonProperty("synonyms")
        private List<String> synonyms;
        @com.fasterxml.jackson.annotation.JsonProperty("antonyms")
        private List<String> antonyms;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class DefinitionsDTO {
            @com.fasterxml.jackson.annotation.JsonProperty("definition")
            private String definition;
            @com.fasterxml.jackson.annotation.JsonProperty("synonyms")
            private List<String> synonyms;
            @com.fasterxml.jackson.annotation.JsonProperty("antonyms")
            private List<String> antonyms;
        }
    }
}
