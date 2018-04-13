package edu.ithaca.group5;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IssueTest {
    @Test
    void setSolvedTest() {
        Issue issue = new Issue("issuename", "Some description of an issue", "");
        assertFalse(issue.solved);
        issue.setSolved();
        assertTrue(issue.solved);
        issue.setSolved();
        assertTrue(issue.solved);
    }

    @Test
    void setOldTest() {
        Issue issue = new Issue("issuename", "Some description of an issue", "");
        assertTrue(issue.isNew);
        issue.setOld();
        assertFalse(issue.isNew);
        issue.setOld();
        assertFalse(issue.isNew);
    }
}
