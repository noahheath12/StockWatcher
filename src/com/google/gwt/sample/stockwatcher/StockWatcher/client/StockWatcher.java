/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.google.gwt.sample.stockwatcher.StockWatcher.client;

import com.google.gwt.user.client.Random;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widget.client.TextButton;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;

import java.util.*;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcher implements EntryPoint {
	private VerticalPanel mainPanel;
	private FlexTable stocksFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newSymbolTextBox;
	private Button addButton;
	private Label lastUpdatedLabel;
	private ArrayList<String> stocks = new ArrayList<String>();
	private static final int REFRESH_INTERVAL = 5000;
	private RootPanel rootPanel;
	
	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		
		mainPanel = new VerticalPanel();
		rootPanel.add(mainPanel, 62, 0);
		mainPanel.setSize("297px", "300px");
		
		Image image = new Image("images/googlecode.png");
		mainPanel.add(image);
		image.setSize("228px", "87px");
		
		Label lblNewLabel = new Label("Stock Watcher");
		lblNewLabel.setStyleName("gwt-Label-StockWatcher");
		mainPanel.add(lblNewLabel);
		
		stocksFlexTable = new FlexTable();
		stocksFlexTable.setTitle("Stock Watcher");
		stocksFlexTable.setText(0,  0,  "Symbol");
		stocksFlexTable.setText(0,  1,  "Price");
		stocksFlexTable.setText(0,  2,  "Change");
		stocksFlexTable.setText(0,  3,  "Remove");
		mainPanel.add(stocksFlexTable);
		stocksFlexTable.setSize("233px", "39px");
		
		addPanel = new HorizontalPanel();
		mainPanel.add(addPanel);
		addPanel.setSize("240px", "41px");
		
		newSymbolTextBox = new TextBox();
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER){
						addStock();
				}
			}
		});
		newSymbolTextBox.setFocus(true);
		addPanel.add(newSymbolTextBox);
		
		addButton = new Button("New button");
		addButton.setStyleName("gwt-Button-Add");
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addStock();
			}
		});
		addButton.setText("Add");
		addPanel.add(addButton);
		addButton.setSize("61px", "38px");
		
		lastUpdatedLabel = new Label("New label");
		mainPanel.add(lastUpdatedLabel);
		
		Timer refreshTimer = new Timer() {
			public void run()
			{
				refreshWatchList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

	}
	private void refreshWatchList() {
		final double MAX_PRICE = 100.0; // $100.00
		final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

		StockPrice[] prices = new StockPrice[stocks.size()];
		for (int i = 0; i < stocks.size(); i++)
		{
			double price = Random.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE
					* (Random.nextDouble() * 2.0 - 1.0);

			prices[i] = new StockPrice((String) stocks.get(i), price, change);
		}

		updateTable(prices);
		
	}
	private void updateTable(StockPrice stockPrice)	{
		// make sure the stock is still in our watch list
		if (!stocks.contains(stockPrice.getSymbol()))
		{
			return;
		}

		int row = stocks.indexOf(stockPrice.getSymbol()) + 1;

		// Format the data in the Price and Change fields.
		String priceText = NumberFormat.getFormat("#,##0.00").format(stockPrice.getPrice());
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(stockPrice.getChange());
		String changePercentText = changeFormat.format(stockPrice.getChangePercent());

		// Populate the Price and Change fields with new data.
		stocksFlexTable.setText(row, 1, priceText);
		stocksFlexTable.setText(row, 2, changeText + " (" + changePercentText + "%)");
	}
	@SuppressWarnings("deprecation")
	private void updateTable(StockPrice[] prices) {
		for (int i = 0; i < prices.length; i++)
		{
			updateTable(prices[i]);
		}

		// change the last update timestamp
		lastUpdatedLabel.setText("Last update : "
			+ DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date()));
		
	}
	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
	    newSymbolTextBox.setFocus(true);

	    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
	    if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
	      Window.alert("'" + symbol + "' is not a valid symbol.");
	      newSymbolTextBox.selectAll();
	      return;
	    }

	    newSymbolTextBox.setText("");
	    
	    if (stocks.contains(symbol))
	        return;

	    // add the stock to the list
	    int row = stocksFlexTable.getRowCount();
	    stocks.add(symbol);
	    stocksFlexTable.setText(row, 0, symbol);

	    // add button to remove this stock from the list
	    Button removeStock = new Button("x");
	    removeStock.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {					
	        int removedIndex = stocks.indexOf(symbol);
	        stocks.remove(removedIndex);
	        stocksFlexTable.removeRow(removedIndex + 1);
	    }
	    });
	    stocksFlexTable.setWidget(row, 3, removeStock);
		
	}
}
