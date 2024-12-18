package org.finance.ui.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.ui.contact.Contact;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** @noinspection ALL*/
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<Contact> originalContacts;
    private List<Contact> filteredContacts;
    private final OnContactClickListener listener;

    public ContactAdapter(List<Contact> contacts, OnContactClickListener listener) {
        this.originalContacts = contacts;
        this.filteredContacts = new ArrayList<>(contacts);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = filteredContacts.get(position);
        holder.bind(contact, listener);
    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    public void filter(String query) {
        filteredContacts.clear();
        if (query.isEmpty()) {
            filteredContacts.addAll(originalContacts);
        } else {
            for (Contact contact : originalContacts) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase())
                        || contact.getPhone().contains(query)) {
                    filteredContacts.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView phoneTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textContactName);
            phoneTextView = itemView.findViewById(R.id.textContactPhone);
        }

        public void bind(Contact contact, OnContactClickListener listener) {
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());
            itemView.setOnClickListener(v -> listener.onContactClick(contact));
        }
    }

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }
}