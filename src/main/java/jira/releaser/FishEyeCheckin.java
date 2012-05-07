package jira.releaser;

import nu.xom.Element;
import nu.xom.Node;

public class FishEyeCheckin {

    private final int revision;
    private final String path;

    public FishEyeCheckin(final Node fileRevisionNode) {
        this((Element) fileRevisionNode);
    }

    FishEyeCheckin(final Element fileRevisionElement) {
        revision = Integer.parseInt(fileRevisionElement.getAttribute("rev").getValue());
        path = fileRevisionElement.getAttribute("path").getValue();
    }

    public int getRevision() {
        return revision;
    }

    public String getPath() {
        return path;
    }

}
