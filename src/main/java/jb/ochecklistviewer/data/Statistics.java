package jb.ochecklistviewer.data;

import lombok.Getter;


/**
 * All numbers are related to current YAML file read from local or downloaded
 * from FTP. So it can change each new fetch.
 */
@Getter
public class Statistics {

    // Number of records/items in YAML file
    private int yamlItems;

    // Number of YAML records with "Started OK" status
    private int starts;

    // Number of YAML records with "DNS" status
    private int dnses;

    // Number of YAML records with "LATE START" status
    private int lateStarts;

    // Number of YAML records with non-null NewCard changeLog item
    private int cardChanges;

    // Number of YAML records with non-null 'comment'
    private int comments;


    @Override
    public String toString() {
        return "records: %d, starts: %d, dnses: %d, late-starts: %d, card-changes: %d, comments: %d"
                .formatted(yamlItems, starts, dnses, lateStarts, cardChanges, comments);
    }


    void yamlItemsInc() {
        yamlItems++;
    }


    void startsInc() {
        starts++;
    }


    void dnsesInc() {
        dnses++;
    }


    void lateStartsInc() {
        lateStarts++;
    }


    void cardChangesInc() {
        cardChanges++;
    }


    void commentsInc() {
        comments++;
    }

}
