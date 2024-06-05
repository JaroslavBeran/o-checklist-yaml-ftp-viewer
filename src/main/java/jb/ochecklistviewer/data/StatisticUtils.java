package jb.ochecklistviewer.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatisticUtils {


    public static Statistics calculate(ChecklistReport report) {
        if (report == null) {
            return new Statistics();
        }

        var stat = new Statistics();
        var runners = report.getRunners();
        runners.forEach(runnerData -> {
            stat.yamlItemsInc();

            var runner = runnerData.getRunner();
            switch (runner.getStartStatus()) {
                case STARTED_OK -> stat.startsInc();
                case DNS -> stat.dnsesInc();
                case LATE_START -> stat.lateStartsInc();
            }

            if (runner.getComment() != null && !runner.getComment().isEmpty()) {
                stat.commentsInc();
            }

            if (runner.getNewCard() != null) {
                stat.cardChangesInc();
            }
        });

        return stat;
    }
}
