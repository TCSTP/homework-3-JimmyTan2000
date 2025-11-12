package tcs.app.dev.homework1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tcs.app.dev.homework1.data.Cart
import tcs.app.dev.homework1.data.Discount
import tcs.app.dev.homework1.data.Shop
// New imports
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Euro
import tcs.app.dev.homework1.data.Item
import tcs.app.dev.homework1.data.MockData
import tcs.app.dev.homework1.data.minus
import tcs.app.dev.homework1.data.plus
import tcs.app.dev.homework1.data.update
import tcs.app.dev.homework1.data.times

/**
 * # Homework 3 — Shop App
 *
 * Build a small shopping UI with ComposeUI using the **example data** from the
 * `tcs.app.dev.homework.data` package (items, prices, discounts, and ui resources).
 * The goal is to implement three tabs: **Shop**, **Discounts**, and **Cart**.
 *
 * ## Entry point
 *
 * The composable function [ShopScreen] is your entry point that holds the UI state
 * (selected tab and the current `Cart`).
 *
 * ## Data
 *
 * - Use the provided **example data** and data types from the `data` package:
 *   - `Shop`, `Item`, `Discount`, `Cart`, and `Euro`.
 *   - There are useful resources in `res/drawable` and `res/values/strings.xml`.
 *     You can add additional ones.
 *     Do **not** hard-code strings in the UI!
 *
 * ## Requirements
 *
 * 1) **Shop item tab**
 *    - Show all items offered by the shop, each row displaying:
 *      - item image + name,
 *      - item price,
 *      - an *Add to cart* button.
 *    - Tapping *Add to cart* increases the count of that item in the cart by 1.
 *
 * 2) **Discount tab**
 *    - Show all available discounts with:
 *      - an icon + text describing the discount,
 *      - an *Add to cart* button.
 *    - **Constraint:** each discount can be added **at most once**.
 *      Disable the button (or ignore clicks) for discounts already in the cart.
 *
 * 3) **Cart tab**
 *    - Only show the **Cart** tab contents if the cart is **not empty**. Within the cart:
 *      - List each cart item with:
 *        - image + name,
 *        - per-row total (`price * amount`),
 *        - an amount selector to **increase/decrease** the quantity (min 0, sensible max like 99).
 *      - Show all selected discounts with a way to **remove** them from the cart.
 *      - At the bottom, show:
 *        - the **total price** of the cart (items minus discounts),
 *        - a **Pay** button that is enabled only when there is at least one item in the cart.
 *      - When **Pay** is pressed, **simulate payment** by clearing the cart and returning to the
 *        **Shop** tab.
 *
 * ## Navigation
 * - **Top bar**:
 *      - Title shows either the shop name or "Cart".
 *      - When not in Cart, show a cart icon.
 *        If you feel fancy you can add a badge to the icon showing the total count (capped e.g. at "99+").
 *      - The cart button is enabled only if the cart contains items. In the Cart screen, show a back
 *        button to return to the shop.
 *
 * - **Bottom bar**:
*       - In Shop/Discounts, show a 2-tab bottom bar to switch between **Shop** and **Discounts**.
*       - In Cart, hide the tab bar and instead show the cart bottom bar with the total and **Pay**
*         action as described above.
 *
 * ## Hints
 * - Keep your cart as a single source of truth and derive counts/price from it.
 *   Rendering each list can be done with a `LazyColumn` and stable keys (`item.id`, discount identity).
 * - Provide small reusable row components for items, cart rows, and discount rows.
 *   This keeps the screen implementation compact.
 *
 * ## Bonus (optional)
 * Make the app feel polished with simple animations, such as:
 * - `AnimatedVisibility` for showing/hiding the cart,
 * - `animateContentSize()` on rows when amounts change,
 * - transitions when switching tabs or updating the cart badge.
 *
 * These can help if want you make the app feel polished:
 * - [NavigationBar](https://developer.android.com/develop/ui/compose/components/navigation-bar)
 * - [Card](https://developer.android.com/develop/ui/compose/components/card)
 * - [Swipe to dismiss](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/swipe-to-dismiss)
 * - [App bars](https://developer.android.com/develop/ui/compose/components/app-bars#top-bar)
 * - [Pager](https://developer.android.com/develop/ui/compose/layouts/pager)
 *
 */

// Enum to manage the state of the bottom navigation tabs
private enum class ShopTab {
    Shop, Discounts
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    shop: Shop,
    availableDiscounts: List<Discount>,
    modifier: Modifier = Modifier
) {
    var cart by rememberSaveable { mutableStateOf(Cart(shop = shop)) }
    var selectedTab by rememberSaveable { mutableStateOf(ShopTab.Shop) }
    var showCartScreen by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (showCartScreen) R.string.title_cart else R.string.name_shop
                        )
                    )
                },
                navigationIcon = {
                    if (showCartScreen) {
                        IconButton(onClick = { showCartScreen = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.description_go_to_shop)
                            )
                        }
                    }
                },
                actions = {
                    if (!showCartScreen) {
                        IconButton(
                            onClick = { showCartScreen = true },
                            // Enable cart button only if items are in the cart
                            enabled = cart.itemCount > 0u
                        ) {
                            BadgedBox(
                                badge = {
                                    // Show badge only if cart is not empty
                                    if (cart.totalCount > 0u) {
                                        Badge {
                                            val count = cart.totalCount
                                            Text(
                                                if (count > 99u) {
                                                    stringResource(R.string.more_than_99)
                                                } else {
                                                    count.toString()
                                                }
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = stringResource(R.string.description_go_to_cart)
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showCartScreen) {
                // In Cart: Show Pay button and Total
                CartBottomBar(
                    cart = cart,
                    onPay = {
                        // Simulate payment: clear cart and return to shop
                        cart = Cart(shop = shop)
                        showCartScreen = false
                        selectedTab = ShopTab.Shop
                    }
                )
            } else {
                // In Shop/Discounts: Show 2-tab navigation
                ShopBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    ) { innerPadding ->
        if (showCartScreen) {
            // Show Cart Content
            // Requirement 3: Only show if not empty.
            // My navigation logic (disabling cart button) already prevents
            // navigating to an empty cart. If user empties cart while in it,
            // this LazyColumn will just be empty, which is fine.
            CartContent(
                cart = cart,
                onCartChanged = { updatedCart -> cart = updatedCart },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            // Show Shop/Discounts Content based on selected tab
            ShopContent(
                selectedTab = selectedTab,
                shop = shop,
                availableDiscounts = availableDiscounts,
                cart = cart,
                onAddItem = { item -> cart += item },
                onAddDiscount = { discount -> cart += discount },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// Main content for Shop and Discounts tabs
@Composable
private fun ShopContent(
    selectedTab: ShopTab,
    shop: Shop,
    availableDiscounts: List<Discount>,
    cart: Cart,
    onAddItem: (Item) -> Unit,
    onAddDiscount: (Discount) -> Unit,
    modifier: Modifier = Modifier
) {
    when (selectedTab) {
        ShopTab.Shop -> ShopList(
            shop = shop,
            onAddItem = onAddItem,
            modifier = modifier
        )

        ShopTab.Discounts -> DiscountList(
            availableDiscounts = availableDiscounts,
            cart = cart,
            onAddDiscount = onAddDiscount,
            modifier = modifier
        )
    }
}

/**
 * Requirement 1: Shop item tab
 */
@Composable
private fun ShopList(
    shop: Shop,
    onAddItem: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(shop.items.toList(), key = { it.id }) { item ->
            shop.prices[item]?.let { price ->
                ShopItemRow(
                    item = item,
                    price = price,
                    onAddItem = { onAddItem(item) }
                )
            }
        }
    }
}

@Composable
private fun ShopItemRow(
    item: Item,
    price: Euro,
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(MockData.getImage(item)),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(MockData.getName(item)),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = price.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Button(onClick = onAddItem) {
                Text(stringResource(R.string.description_add_to_cart))
            }
        }
    }
}

/**
 * Requirement 2: Discount tab
 */
@Composable
private fun DiscountList(
    availableDiscounts: List<Discount>,
    cart: Cart,
    onAddDiscount: (Discount) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(availableDiscounts, key = { it.toString() }) { discount ->
            // Constraint: each discount can be added at most once.
            val alreadyInCart = discount in cart.discounts
            DiscountRow(
                discount = discount,
                isEnabled = !alreadyInCart,
                onAddDiscount = { onAddDiscount(discount) }
            )
        }
    }
}

@Composable
private fun DiscountRow(
    discount: Discount,
    isEnabled: Boolean,
    onAddDiscount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = getDiscountDescription(discount),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Button(onClick = onAddDiscount, enabled = isEnabled) {
                Text(stringResource(R.string.description_add_to_cart))
            }
        }
    }
}

/**
 * Requirement 3: Cart tab
 */
@Composable
private fun CartContent(
    cart: Cart,
    onCartChanged: (Cart) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // List cart items
        items(cart.items.entries.toList(), key = { it.key.id }) { (item, amount) ->
            cart.shop.prices[item]?.let { price ->
                CartItemRow(
                    item = item,
                    amount = amount,
                    price = price,
                    onAmountChanged = { newAmount ->
                        val updatedCart = if (newAmount == 0u) {
                            cart - item // Remove item if amount is 0
                        } else {
                            cart.update(item to newAmount) // Update amount
                        }
                        onCartChanged(updatedCart)
                    }
                )
            }
        }

        // Spacer if both items and discounts are present
        if (cart.items.isNotEmpty() && cart.discounts.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                // Maybe can add a Text("Discounts") header here (?)
            }
        }

        // List selected discounts
        items(cart.discounts, key = { it.toString() }) { discount ->
            CartDiscountRow(
                discount = discount,
                onRemove = { onCartChanged(cart - discount) }
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: Item,
    amount: UInt,
    price: Euro,
    onAmountChanged: (UInt) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(MockData.getImage(item)),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(MockData.getName(item)),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = (price * amount).toString(), // Per-row total
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            AmountSelector(
                amount = amount,
                onAmountChanged = onAmountChanged
            )
        }
    }
}

@Composable
private fun AmountSelector(
    amount: UInt,
    onAmountChanged: (UInt) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button
        IconButton(
            onClick = { onAmountChanged(amount - 1u) },
            enabled = amount > 0u // Min 0
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.description_decrease_amount)
            )
        }

        Text(
            text = amount.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        // Increase button
        IconButton(
            onClick = { onAmountChanged(amount + 1u) },
            enabled = amount < 99u // Sensible max 99
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.description_increase_amount)
            )
        }
    }
}

@Composable
private fun CartDiscountRow(
    discount: Discount,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = getDiscountDescription(discount),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.description_remove_from_cart)
                )
            }
        }
    }
}

/**
 * Requirement 3 & 6: Cart bottom bar
 */
@Composable
private fun CartBottomBar(
    cart: Cart,
    onPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp // Adds a shadow to separate from content
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.total_price, cart.price.toString()),
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                onClick = onPay,
                // Enabled only when there is at least one item
                enabled = cart.itemCount > 0u
            ) {
                Text(stringResource(R.string.label_pay))
            }
        }
    }
}

/**
 * Requirement 6: Bottom bar for Shop/Discounts
 */
@Composable
private fun ShopBottomNavigation(
    selectedTab: ShopTab,
    onTabSelected: (ShopTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = selectedTab == ShopTab.Shop,
            onClick = { onTabSelected(ShopTab.Shop) },
            icon = { Icon(Icons.Default.Store, contentDescription = null) },
            label = { Text(stringResource(R.string.label_shop)) }
        )
        NavigationBarItem(
            selected = selectedTab == ShopTab.Discounts,
            onClick = { onTabSelected(ShopTab.Discounts) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
            label = { Text(stringResource(R.string.label_discounts)) }
        )
    }
}

/**
 * Helper composable to get a human-readable string for a discount.
 */
@Composable
private fun getDiscountDescription(discount: Discount): String {
    return when (discount) {
        is Discount.Bundle -> stringResource(
            R.string.pay_n_items_and_get,
            discount.amountItemsPay,
            stringResource(MockData.getName(discount.item)),
            discount.amountItemsGet
        )

        is Discount.Fixed -> stringResource(
            R.string.amount_off,
            discount.amount.toString()
        )

        is Discount.Percentage -> stringResource(
            R.string.percentage_off,
            discount.value.toString()
        )
    }
}
