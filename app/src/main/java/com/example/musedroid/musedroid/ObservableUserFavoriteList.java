package com.example.musedroid.musedroid;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by gdev on 30/9/2017.
 */

public class ObservableUserFavoriteList {


    public static class ObservableList<Museum> {

        protected List<Museum> list;
        protected PublishSubject<Museum> onAdd;

        public ObservableList() {
            this.list = new ArrayList<Museum>();
            this.onAdd = PublishSubject.create();
        }

        public void add(Museum value) {
            list.add(value);
            onAdd.onNext(value);
        }

        public Observable<Museum> getObservable() {
            return onAdd;
        }

        public int getItemCount() {
            return this.list.size();
        }

        public Museum getItem(int position) {
            return list.get(position);
        }
        public Observable<Museum> getCurrentList(int position) {
            Museum museum = list.get(position);
            return Observable.just(museum);
        }
    }
}
