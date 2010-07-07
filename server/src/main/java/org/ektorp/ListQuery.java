package org.ektorp;

/**
 * Simple ViewQuery extension that allows to query CouchDB list functions.
 */
public class ListQuery extends ViewQuery {

    private final String listName;

    /**
     * Creates the query object with passed list function name
     * @param listName the name of the list function to query for
     */
    public ListQuery(String listName) {
        this.listName = listName;
    }

    /**
     * Return the list function name
     * @return the list function name
     */
    public String getListName() {
        return listName;
    }

    /**
     * Overrides ViewQuery's behavior in order to compute the appropriate
     * query path.
     * @return the path to the be used to query the list function
     */
    @Override
    public String buildQuery() {
        String query = super.buildQuery();
        return query.replaceAll("_view", "_list/" + listName);
    }
}
