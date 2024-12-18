package org.finance.ui.contact;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactFragment extends BaseFragment {

    private EditText editTextPhone;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View createView(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.select_contact, null);

        editTextPhone = rootView.findViewById(R.id.editTextPhone);

        editTextPhone.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPhone.getRight() - editTextPhone.getCompoundDrawables()[2].getBounds().width())) {
                    showContactsBottomSheet();
                    return true;
                }
            }
            return false;
        });

        return rootView;
    }

    private void showContactsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_contacts, null);
        bottomSheetDialog.setContentView(sheetView);
        RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerViewContacts);
        EditText searchInput = sheetView.findViewById(R.id.editTextSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Contact> contacts = getContacts();
        ContactAdapter adapter = new ContactAdapter(contacts, contact -> {
            if (editTextPhone != null) {
                editTextPhone.setText(contact.getPhone());
            }
            bottomSheetDialog.dismiss();
        });
        recyclerView.setAdapter(adapter);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        sheetView.post(() -> {
            View parent = (View) sheetView.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            behavior.setPeekHeight(sheetView.getHeight());
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        });
        bottomSheetDialog.show();
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        HashMap<String, String> uniqueContacts = new HashMap<>();

        ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (!uniqueContacts.containsKey(phone)) {
                    uniqueContacts.put(phone, name);
                }
            }
            cursor.close();
        }

        for (HashMap.Entry<String, String> entry : uniqueContacts.entrySet()) {
            contacts.add(new Contact(entry.getValue(), entry.getKey()));
        }
        contacts.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        return contacts;
    }

}
