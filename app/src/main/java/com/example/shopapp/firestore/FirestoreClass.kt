package com.example.shopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shopapp.models.*
import com.example.shopapp.ui.i.activities.*
import com.example.shopapp.ui.i.fragments.DashboardFragment
import com.example.shopapp.ui.i.fragments.OrdersFragment
import com.example.shopapp.ui.i.fragments.ProductsFragment
import com.example.shopapp.ui.i.fragments.SoldProductsFragment
import com.example.shopapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        //Creation de database
        /*
        The "users" is collection name. If the collection is already created then it will not create the same again.
        Document ID for users fields. Here the document it is the User ID.
        userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of repplacing the fields.
        sirve para unir el user con la autenticación de firebase y el set para que se le pueda añadir luego gender o image
         */
        mFireStore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //Here call a function of base activity for transferring the result to it
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user", e)

            }
    }

    fun getCurrentUserID(): String {
        //We can get with this function the ID of the current user
        //An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        //Here we pass the collection name from which we wants the data
        mFireStore.collection(Constants.USERS)
            //The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object
                try {

                    val user =
                        document.toObject(User::class.java)!! //convertimos lo que recibimos del data base en un objeto user para trabajar con él

                    //Creating and instance from sharedpreferences
                    val sharedPreferences = activity.getSharedPreferences(
                        Constants.MYSHOPPAL_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    //KEY: looged_in_username - VALUE: firstName + lastName

                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    //START
                    when (activity) {
                        is LoginActivity -> {
                            //Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingsActivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userDetailsSuccess(user)
                        }
                    }
                    //END
                }
                catch (e:Exception){
                    Toast.makeText(activity, "Error al encontrar usuario", Toast.LENGTH_SHORT).show()
                }


            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }

                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHasMap: HashMap<String, Any>) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHasMap)
            .addOnSuccessListener {

                when (activity) {
                    is UserProfileActivity -> {
                        //Hide the progress dialog if there is any error. And print the error in log
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        //Hide the progress dialog if there is any error. And print the error in log
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the user details", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //Le estamos poniendo el nombre de la constante USER_PROFILE_IMAGE, despues un numero que es el tiempo en milisegundos que pasa
        // y despues el nombre de la extensión que tenga
        //Por tanto se quedaría así: User_Profile_Image123456765432.jpg
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            //The image upload is success
            Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            //Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())
                when (activity) {
                    is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                    is AddProductActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
        }
            .addOnFailureListener { exception ->
                //Hide the progress dialog if there is any error. And print the error in log

                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, exception.message, exception)
            }

    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                //Here call a function of base activity for transferring the result to it
                activity.productUploadSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error uploading the product")
            }
    }

    //Asignamos el id del current user a la tabla product
    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                //Obtenemos el id del producto y lo añadimos a la lista de productos
                //asignamos el id del producto
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }

            }

    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error with dashboard fragment", e)
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product",
                    e
                )
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the product details", e)

            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: Cart) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item",
                    e
                )
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing car list",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Cart> = ArrayList()

                for (i in document.documents) {
                    val cartItem = i.toObject(Cart::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing car list",
                    e
                )
            }

    }

    fun getAllProductsList(activity: Activity) {

        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->

                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener { e ->

                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {

                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
            }

    }

    fun updateMyCart(context: Context, cart_id: String, itemHasMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHasMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {

                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
            }

    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address",
                    e
                )
            }
    }

    fun getAddressesList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the address list", e)
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener { document ->

                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating the address", e)
            }

    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener { document ->

                activity.deleteAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while deleting the address", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating an order",
                    e
                )
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<Cart>, order: Order) {

        val writeBatch = mFireStore.batch()

        // Prepare the sold product details
        for (cart in cartList) {

            //Creating the soldProduct
            val soldProduct = SoldProduct(
                cart.product_owner_id,
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()
            //restar lo que cogemos en el carro del stock
            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cart.product_id)
            //actualizar el documento product_id
            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }
        //commit the writeBatch
        writeBatch.commit().addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->

            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.", e
            )
        }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }
                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list", e)
            }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        // The collection name for SOLD PRODUCTS
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold products in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Sold Products ArrayList.
                val list: ArrayList<SoldProduct> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }

                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }


}