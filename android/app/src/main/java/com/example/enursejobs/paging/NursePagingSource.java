//package com.example.enursejobs.paging;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.paging.ListenableFuturePagingSource;
//import androidx.paging.PagingState;
//
//import com.google.common.util.concurrent.ListenableFuture;
//import com.example.enursejobs.api.RemoteDataSource;
//import com.example.enursejobs.db.EntityLocal.NurseEntity;
//
//import java.util.concurrent.Executor;
//
//public class NursePagingSource extends ListenableFuturePagingSource<Integer, NurseEntity>
//{
//    @NonNull
//    private RemoteDataSource mRemoteDataSource;
//    @NonNull
//    private String mQuery;
//    @NonNull
//    private Executor mBgExecutor;
//
//    NursePagingSource(
//        @NonNull final RemoteDataSource remoteDataSource,
//        @NonNull String query, @NonNull Executor bgExecutor) {
//            mRemoteDataSource = remoteDataSource;
//            mQuery = query;
//            mBgExecutor = bgExecutor;
//    }
//    @Nullable
//    @Override
//    public Integer getRefreshKey(@NonNull PagingState<Integer, NurseEntity> pagingState) {
//        // Try to find the page key of the closest page to anchorPosition, from
//        // either the prevKey or the nextKey, but you need to handle nullability
//        // here:
//        //  * prevKey == null -> anchorPage is the first page.
//        //  * nextKey == null -> anchorPage is the last page.
//        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
//        //    just return null.
//        Integer anchorPosition = pagingState.getAnchorPosition();
//        if (anchorPosition == null) {
//            return null;
//        }
//
//        LoadResult.Page<Integer, NurseEntity> anchorPage = pagingState.closestPageToPosition(anchorPosition);
//        if (anchorPage == null) {
//            return null;
//        }
//
//        Integer prevKey = anchorPage.getPrevKey();
//        if (prevKey != null) {
//            return prevKey + 1;
//        }
//
//        Integer nextKey = anchorPage.getNextKey();
//        if (nextKey != null) {
//            return nextKey - 1;
//        }
//
//        return null;
//    }
//
//    @NonNull
//    @Override
//    public ListenableFuture<LoadResult<Integer, NurseEntity>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
//        // Start refresh at page 1 if undefined.
//        Integer nextPageNumber = loadParams.getKey();
//        if (nextPageNumber == null) {
//            nextPageNumber = 1;
//        }
//        ListenableFuture<LoadResult<Integer, NurseEntity>> pageFuture =
//                Futures.transform(mRemoteDataSource.fetchNursesRemote(nurses->{}),
//                        this::toLoadResult, mBgExecutor);
//        ListenableFuture<LoadResult<Integer, NurseEntity>> partialLoadResultFuture =
//                Futures.catching(pageFuture, HttpException.class,
//                        LoadResult.Error::new, mBgExecutor);
//        return Futures.catching(partialLoadResultFuture,
//                IOException.class, LoadResult.Error::new, mBgExecutor);
//    }
//
//    private LoadResult<Integer, NurseEntity> toLoadResult(@NonNull SearchUserResponse response) {
//        return new LoadResult.Page<>(response.getUsers(),
//                null, // Only paging forward.
//                response.getNextPageNumber(),
//                LoadResult.Page.COUNT_UNDEFINED,
//                LoadResult.Page.COUNT_UNDEFINED);
//    }
//}
