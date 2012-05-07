package jira.releaser;

import java.io.IOException;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import com.google.common.collect.Lists;

public class FishEyeConnection {

    private final String searchUrl;
    private final String loginUrl;
    private final String repoUrl;
    private final Builder parser;
    private String token;
    private final Arguments arguments;
    private String fishEyeRepo;

    private FishEyeConnection(final Arguments arguments, final Builder builder) {
        this.arguments = arguments;
        this.parser = builder;
        this.repoUrl = arguments.getFisheyeUrl() + "rest-service-fe/repositories-v1";
        this.searchUrl = arguments.getFisheyeUrl() + "rest-service-fe/search-v1/query/";
        this.loginUrl = arguments.getFisheyeUrl() + "rest-service/auth-v1/login";
    }

    public static FishEyeConnection login(final Arguments arguments, final Builder builder) throws Exception {

        final FishEyeConnection fishEyeConnection = new FishEyeConnection(arguments, builder);
        fishEyeConnection.loginToFishEye();
        fishEyeConnection.setRepositorys();
        return fishEyeConnection;
    }


    private void setRepositorys() throws ValidityException, ParsingException, IOException {
        final Document doc = parser.build(repoUrl + "?&FEAUTH=" + token);
        final Element element = (Element) doc.query("//repository[@enabled='true']").get(0);
        this.fishEyeRepo = element.getAttributeValue("name");
    }

    List<FishEyeCheckin> getCheckinsWhereCommentContains(final String issueKey) {
        Document doc = null;
        try {
            doc = parser.build(searchUrl + fishEyeRepo + "?query=select%20revisions%20where%20comment%20matches%20%22" +
                    issueKey + "%22&FEAUTH=" + token);
        } catch (final ParsingException e) {
            System.out.println("Unable to parse XML response from FishEye query. Exiting.");
            e.printStackTrace();
            System.exit(-1);
        } catch (final IOException e) {
            System.out.println("Could not connect to FishEye. Exiting.");
            e.printStackTrace();
            System.exit(-1);
        }
        return createListFrom(doc);
    }

    private List<FishEyeCheckin> createListFrom(final Document doc) {
        final Nodes nodes = doc.query("//fileRevisionKey");
        final List<FishEyeCheckin> fileRevisions = Lists.newArrayList();
        for (int i = 0; i < nodes.size(); i++) {
            fileRevisions.add(new FishEyeCheckin(nodes.get(i)));
        }
        return fileRevisions;
    }

    private void loginToFishEye() throws ValidityException, ParsingException, IOException {
        final Document doc1 = parser.build(loginUrl + "?userName=" +
                arguments.getUsername() + "&password=" + arguments.getPassword());
        final Nodes nodes = doc1.query("//token/text()");
        token = nodes.get(0).getValue();
    }
}
