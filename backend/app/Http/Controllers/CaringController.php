<?php

namespace App\Http\Controllers;

use App\Http\Requests\CaringStoreRequest;
use App\Jobs\NotificationJob;
use App\Models\Caring;
use App\Models\Caringtype;
use App\Models\Nurse;
use App\Models\Patient;
use App\Tables\Carings;
use App\Traits\CaringTrait;
use Illuminate\Http\Request;
use ProtoneMedia\Splade\Facades\Toast;
use ProtoneMedia\Splade\SpladeTable;
use Spatie\QueryBuilder\QueryBuilder;

class CaringController extends Controller
{
    use CaringTrait;

    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('caring.index',[
            'carings' => Carings::class
        ]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('caring.create')
            ->with('nurses',Nurse::all())
            ->with('caringtypes',Caringtype::all())
            ->with('patients',Patient::all());
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(CaringStoreRequest $request)
    {
        $caring = new caring();
        $this->saveRequestData($request, $caring);
        Toast::success('Caring Added Successfully');

        //queue Firebase FCM Notification
        $nurse = Nurse::where('id', $request->nurse_id)->whereNotNull('fcm_token')->first();
        if($nurse != null){
            $fcmTokens = $nurse->pluck('fcm_token')->toArray();
            NotificationJob::dispatch($nurse, $fcmTokens);
        }

        return redirect()->back();
    }

    /**
     * Display the specified resource.
     */
    public function show(Caring $caring)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Caring $caring)
    {
        return view('caring.edit',compact('caring'))
            ->with('nurses',Nurse::all())
            ->with('caringtypes',Caringtype::all())
            ->with('patients',Patient::all());
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(CaringStoreRequest $request, Caring $caring)
    {
        $this->saveRequestData($request, $caring);
        Toast::success('Caring Updated Successfully');
        return redirect()->back();
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Caring $caring)
    {
        $caring->delete();
        Toast::success('Caring Deleted Successfully');
        return redirect()->back();
    }

    public function check(int $id){
        $caring = Caring::findorFail($id);
        $caring->is_finished = true;
        $caring->save();
        $caring->delete();
        Toast::success('Caring Status Changed');
        return redirect()->back();
    }

    public function restore(int $id){
        $caring = Caring::withTrashed()->findorFail($id);
        $caring->is_finished = false;
        $caring->save();
        $caring->restore(); // restore soft deleted carings
        return redirect()->back(); //redirect to previous page
    }

    public function showTrashed()
    {
        $query = Caring::query()->onlyTrashed();

        $carings = QueryBuilder::for($query)
            ->defaultSort('time')
            ->allowedSorts(['time'])
            ->allowedFilters(['time']);

        return view('caring.trashed', [
            'carings' => SpladeTable::for($carings)
                ->withGlobalSearch()
                ->column('nurse_id','Nurse Name')
                ->column('patient_id','Patient Name')
                ->column('caringtype_id', 'Caring Type',sortable: true, searchable: true)
                ->column('time', 'Time',sortable: true, searchable: true)
                ->column('description','Description')
                ->column('is_finished','Status')
                ->column('deleted_at','Remove Date')
                ->column('action')
                ->paginate(15)
        ]);
    }
    public function forceDelete(int $id)
    {
        Caring::withTrashed()->findorFail($id)->forceDelete();
        Toast::success('Caring Deleted Permanently');
        return redirect()->back();
    }

    public function editTrashed(int $id)
    {
        $caring = Caring::withTrashed()->findorFail($id); // works with id only
        return view('caring.edit',compact('caring'))
            ->with('nurses',Nurse::all())
            ->with('caringtypes',Caringtype::all())
            ->with('patients',Patient::all());
    }

    public function updateTrashed(Request $request, int $id)
    {
        $caring = Caring::withTrashed()->findorFail($id); // works with id only
        $this->saveRequestData($request, $caring);
        Toast::success('Caring Updated Successfully');
        return redirect()->back();
    }
}
