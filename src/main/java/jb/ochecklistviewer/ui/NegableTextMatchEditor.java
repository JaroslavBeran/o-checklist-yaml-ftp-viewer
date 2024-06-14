package jb.ochecklistviewer.ui;

import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.impl.filter.SearchTerm;
import ca.odell.glazedlists.impl.filter.TextMatcher;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import lombok.Getter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class was reused from TextComponentMatcherEditor and added support for negable button.
 * @param <E>
 */
public class NegableTextMatchEditor<E> extends TextMatcherEditor<E> {

    /**
     * the Document that provides the filter values
     */
    private Document document;

    /**
     * the JTextComponent being observed for actions
     */
    private final JTextComponent textComponent;

    private final JToggleButton toggleButton;


    @Getter
    private boolean live;


    private final FilterHandler filterHandler = new FilterHandler();


    public NegableTextMatchEditor(JToggleButton toggleButton, JTextComponent textComponent, TextFilterator<? super E> textFilterator) {
        this(toggleButton, textComponent, textFilterator, true);
    }


    public NegableTextMatchEditor(JToggleButton toggleButton, JTextComponent textComponent, TextFilterator<? super E> textFilterator, boolean live) {
        this(toggleButton, textComponent, textComponent.getDocument(), textFilterator, live);
    }


    /**
     * This private constructor implements the actual construction work and thus
     * ensures that all public constructors agree on the construction logic.
     */
    private NegableTextMatchEditor(JToggleButton toggleButton, JTextComponent textComponent, Document document, TextFilterator<? super E> textFilterator, boolean live) {
        super(textFilterator);

        this.toggleButton = toggleButton;
        this.textComponent = textComponent;
        this.document = document;
        this.live = live;
        registerListeners(live);

        // if the document is non-empty to begin with!
        refilter();
    }

    /**
     * Toggle between filtering by the keystroke and not.
     *
     * @param live <code>true</code> to filter by the keystroke or <code>false</code>
     *             to filter only when {@link java.awt.event.KeyEvent#VK_ENTER Enter} is pressed
     *             within the {@link JTextComponent}. Note that non-live filtering is only
     *             supported if <code>textComponent</code> is a {@link JTextField}.
     */
    public void setLive(boolean live) {
        if (live == this.live) return;
        deregisterListeners(this.live);
        this.live = live;
        registerListeners(this.live);
    }

    /**
     * Listen live or on action performed.
     */
    private void registerListeners(boolean live) {
        if (live) {
            document.addDocumentListener(filterHandler);
        } else {
            if (textComponent == null)
                throw new IllegalArgumentException("Non-live filtering supported only for JTextField (document provided)");
            if (!(textComponent instanceof JTextField))
                throw new IllegalArgumentException("Non-live filtering supported only for JTextField (argument class " + textComponent.getClass().getName() + ")");
            JTextField textField = (JTextField) textComponent;
            textField.addActionListener(filterHandler);
        }

        if (toggleButton != null) {
            toggleButton.addActionListener(filterHandler);
        }

        if (textComponent != null)
            textComponent.addPropertyChangeListener(filterHandler);
    }

    /**
     * Stop listening.
     */
    private void deregisterListeners(boolean live) {
        if (live) {
            document.removeDocumentListener(filterHandler);
        } else {
            JTextField textField = (JTextField) textComponent;
            textField.removeActionListener(filterHandler);
        }

        if (toggleButton != null) {
            toggleButton.addActionListener(filterHandler);
        }

        if (textComponent != null)
            textComponent.removePropertyChangeListener(filterHandler);
    }

    /**
     * A cleanup method which stops this MatcherEditor from listening to
     * changes on the underlying {@link Document}, thus freeing the
     * MatcherEditor or Document to be garbage collected.
     */
    public void dispose() {
        deregisterListeners(live);
    }

    /**
     * Update the filter text from the contents of the Document.
     */
    private void refilter() {
        try {
            final int mode = getMode();
            final String text = document.getText(0, document.getLength());
            final String[] filters;

            // in CONTAINS mode we treat the string as whitespace delimited
            if (mode == CONTAINS)
                filters = text.split("[ \t]");

                // in STARTS_WITH, REGULAR_EXPRESSION, or EXACT modes we use the string in its entirety
            else if (mode == STARTS_WITH || mode == REGULAR_EXPRESSION || mode == EXACT)
                filters = new String[]{text};

            else throw new IllegalStateException("Unknown mode: " + mode);

            // wrap the filter Strings with SearchTerm objects
            final SearchTerm<?>[] searchTerms;
            boolean negated = toggleButton.isSelected();
            if (negated) {
                searchTerms = new SearchTerm[1];
                searchTerms[0] = new SearchTerm<E>(text, true, false, null);
            } else {
                searchTerms = new SearchTerm[filters.length];
                for (int i = 0; i < searchTerms.length; i++) {
                    searchTerms[i] = new SearchTerm<E>(filters[i], false, false, null);
                }
            }

            // adjust the TextMatcher
            setTextMatcher(new TextMatcher<>(searchTerms, getFilterator(), getMode(), getStrategy()));
        } catch (BadLocationException ble) {
            // this shouldn't ever, ever happen
            throw new RuntimeException(ble);
        }
    }

    /**
     * This class responds to any change in the Document by setting the filter
     * text of this TextMatcherEditor to the contents of the Document.
     */
    private class FilterHandler implements DocumentListener, ActionListener, PropertyChangeListener, ChangeListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            refilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            refilter();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            refilter();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            refilter();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("document".equals(evt.getPropertyName())) {
                // stop listening to the old Document
                deregisterListeners(live);

                // start listening to the new Document
                document = textComponent.getDocument();
                registerListeners(live);

                // refilter based on the new Document
                refilter();
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            refilter();
        }
    }
}
