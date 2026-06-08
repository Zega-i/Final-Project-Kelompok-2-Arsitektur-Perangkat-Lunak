package observer;

import model.Proposal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Observer Pattern — Audit log in-memory untuk monitoring dan distributed tracing. */
public class SystemLogObserver implements ProposalObserver {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void onStatusChanged(Proposal proposal, String oldStatus, String newStatus) {
        String time = LocalTime.now().format(TIME_FMT);
        System.out.printf("[OBSERVER] SystemLog     : Proposal %s → %s (%s)%n",
                proposal.getId(), newStatus, time);
    }
}
