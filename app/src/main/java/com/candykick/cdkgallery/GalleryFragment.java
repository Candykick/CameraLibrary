package com.candykick.cdkgallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.candykick.cdkgallery.adapter.GalleryFolderAdapter;
import com.candykick.cdkgallery.adapter.GalleryImageAdapter;
import com.candykick.cdkgallery.data.GalleryFolderData;
import com.candykick.cdkgallery.databinding.FragmentGalleryBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * 참고 자료: https://stackoverflow.com/questions/45400497/how-to-get-list-of-directory-containing-images-in-android
 *          https://ourcstory.tistory.com/83(일부 참고)
 *          https://dktfrmaster.blogspot.com/2016/10/mediastore.html?showComment=1569257526781#c4389278270154132406 (projection 목록)
 *          https://altongmon.tistory.com/886(도움 안되서 삭제함)
 */

/*
 * File의 getPath()와 getAbsolutePath() 차이점 (https://stackoverflow.com/questions/1099300/whats-the-difference-between-getpath-getabsolutepath-and-getcanonicalpath)
 *
 * C:\temp\file.txt - path(O), absolute path(O), canonical path(O)

    .\file.txt - path(O), absolute path(X), canonical path(X)

    C:\temp\myapp\bin\..\\..\file.txt - path(O), absolute path(O), canonical path(X)
 *
 *  getPath(): 현재 폴더 기준 상대적인 파일 위치를 불러오는 듯. (그래서 보통 잘 안 씀)
 *  getAbsolutePath(): 전체 경로나 중간이 생략된 경로를 불러옴. (보통 많이 씀)
 *  getCanonicalPath(): 전체 경로를 불러옴. (쓰는 걸 본 적이 없음)
 */

// 같은 이름의 폴더가 있을 때에도 멀쩡히 처리되는지 살펴보기

public class GalleryFragment extends Fragment {

    private static final int GRID_IMAGE_COUNT = 4;

    FragmentGalleryBinding binding;

    public GalleryFragment() {}

    public interface GalleryListener {
        void receivedGalleryImage(Bitmap bitmap, Uri path);
        void receivedGalleryImage(String path);
    }

    private GalleryListener galleryListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);

        //전체 폴더 중 사진이 들어 있는 폴더 목록을 가져온다.
        //File imageFolders = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM); //DCIM 폴더 내부의 내용(파일 포함)을 가져온다. 이미지를 다 가져오지 않음.
        //File imageFolders = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File[] folderList = imageFolders.listFiles();
        //String[] folders = file.list(): 이런 식으로 하면 폴더 이름만 따로 가져올 수 있다.

        //처음에는 최신순 정렬로 이미지를 보여준다.
        setImageListByDate();

        //정렬 기준 Spinner 리스너 등록
        binding.spGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //최신순 정렬
                        setImageListByDate();
                        break;
                    case 1: //폴더별 정렬
                        setFolderList();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof GalleryListener) {
            galleryListener = (GalleryListener)getActivity();
        }
    }

    private void setImageListByDate() {
        List<String> list = new ArrayList<>();
        //List<Uri> list = new ArrayList<>();
        //List<Bitmap> bitmaps = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, orderBy);
        if(cursor != null && cursor.getCount() != 0) {
            File image;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                image = new File(path);
                if(image.exists() && !list.contains(path)) {
                    list.add(path);
                    //bitmaps.add(BitmapFactory.decodeFile(path));
                }
            }
            cursor.close();
        } else {
            Toast.makeText(getActivity(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        GalleryImageAdapter adapter = new GalleryImageAdapter(getActivity(), list);
        adapter.setOnItemClickListener(new GalleryImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //Toast.makeText(getActivity(), list.get(pos), Toast.LENGTH_SHORT).show();
                galleryListener.receivedGalleryImage(list.get(pos));
            }
        });
        binding.rvGallery.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), GRID_IMAGE_COUNT);
        binding.rvGallery.setLayoutManager(gridLayoutManager);
    }

    private void setFolderList() {
        List<GalleryFolderData> list = new ArrayList<>();
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID};

        Cursor cursor = getActivity().getContentResolver().query(imageUri, projection, null, null, null);
        if(cursor != null && cursor.getCount() != 0) {
            File image;
            ArrayList<String> folderIds = new ArrayList<>();

            //주의: 커서에 있는 데이터는 폴더 순서대로 들어오지 않는다. 생성된 순서나 파일명 등의 순서로 들어온다.
            while(cursor.moveToNext()) {
                String folderName = cursor.getString(cursor.getColumnIndex(projection[0]));
                String imagePath = cursor.getString(cursor.getColumnIndex(projection[1]));
                String folderId = cursor.getString(cursor.getColumnIndex(projection[2]));
                /*String[] paths = imagePath.split("/");
                String path = paths[0];
                for(int i=1;i<paths.length-1;i++) {
                    path += "/";
                    path += paths[i];
                }*/

                image = new File(imagePath);

                //처음으로 탐색하는 폴더인 경우: 폴더 목록에 추가하고, 첫 번째 이미지 정보까지 같이 넣어준다.
                //폴더가 가지고 있는 이미지 갯수는 일단 1개로 넣고, 파일들을 탐색하면서 갯수를 늘려 나간다.
                if(image.exists() && !folderIds.contains(folderId)) {
                    folderIds.add(folderId);

                    GalleryFolderData data = new GalleryFolderData();
                    data.folderName = folderName;
                    data.folderId = folderId;
                    data.folderImageSrc = imagePath;
                    data.folderImageNum = 1;
                    list.add(data);
                }
                //이미 탐색한 적 있는 폴더인 경우: 해당 폴더의 이미지 갯수를 1개 늘린다.
                else if(folderIds.contains(folderId)) {
                    int index = folderIds.indexOf(folderId);
                    list.get(index).folderImageNum++;
                }
            }
            cursor.close();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            binding.rvGallery.setLayoutManager(linearLayoutManager);
            GalleryFolderAdapter adapter = new GalleryFolderAdapter(getActivity(), list);
            adapter.setOnItemClickListener(new GalleryFolderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    setImageListbyFolder(list.get(pos).folderId);
                }
            });
            binding.rvGallery.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImageListbyFolder(@NonNull String folderId) {
        List<String> list = new ArrayList<>();
        //List<Uri> list = new ArrayList<>();
        //List<Bitmap> bitmaps = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_ID+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";
        //String[] selectionArgs = new String[]{MediaStore.Files.FileColumns.MEDIA_TYPE};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, new String[]{folderId}, orderBy);
        if(cursor != null && cursor.getCount() != 0) {
            File image;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                image = new File(path);
                if(image.exists() && !list.contains(path)) {
                    list.add(path);
                    //bitmaps.add(BitmapFactory.decodeFile(path));
                }
            }
            cursor.close();
        } else {
            Toast.makeText(getActivity(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        GalleryImageAdapter adapter = new GalleryImageAdapter(getActivity(), list);
        adapter.setOnItemClickListener(new GalleryImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                galleryListener.receivedGalleryImage(list.get(pos));
                //Toast.makeText(getActivity(), list.get(pos), Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvGallery.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), GRID_IMAGE_COUNT);
        binding.rvGallery.setLayoutManager(gridLayoutManager);
    }
}
