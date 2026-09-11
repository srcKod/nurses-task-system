<?php

namespace App\Http\Controllers;

use App\Http\Requests\NurseStoreRequest;
use App\Http\Requests\ProfileUpdateRequest;
use App\Models\Nurse;
use App\Notifications\SendPushNotification;
use App\Tables\Nurses;
use App\Traits\NurseTrait;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Notification;
use ProtoneMedia\Splade\Facades\Toast;
use ProtoneMedia\Splade\SpladeTable;

class NurseController extends Controller
{
    use NurseTrait;
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('nurse.index', ['nurses' => Nurses::class]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('nurse.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(NurseStoreRequest $request)
    {
        $nurse = new nurse();
        $this->saveRequestData($request, $nurse);
//        $nurse->create([
//            'name'=>$request->name,
//            'gender'=>$request->gender,
//            'phone'=>$request->phone,
//            'is_resigned'=>$request->is_resigned,
//            'is_admin'=>$request->is_admin,
//            'email'=>$request->email,
//            'password'=>Hash::make($request->password)
//        ]);
        Toast::success('Nurse Added Successfully');
        return redirect()->back();
    }

    /**
     * Display the specified resource.
     */
    public function show(nurse $nurse)
    {
        $carings = $nurse->carings;
        foreach ($carings as $caring) {
            foreach ($caring->caringtypes as $caringtype) {
                foreach ($caring->patients as $patient) {
                    $result = [
                        $nurse->name,
                        $caringtype->name,
                        $caringtype->description,
                        $patient->name,
                        $caring->time->toDayDateTimeString(),
                        $caring->description
                    ];
                }
            }
        }
        //return response($result);
        //return view('nurse.index',compact('result'));
        return view('nurse.index', [
            'nurse' => SpladeTable::for($result)
                ->column('name')
                ->column('name')
                ->column('description')
                ->column('name')
                ->column('time')
                ->column('description')
                ->paginate(15),
        ]);
    }

    /**
     * Display the specified resource.
     */
    public function showById(int $id)
    {
        $nurse = Nurse::find($id)->first();
        $carings = $nurse->carings;
        foreach ($carings as $caring) {
            foreach ($caring->caringtypes as $caringtype) {
                foreach ($caring->patients as $patient) {
                    $result = [
                        $nurse->name,
                        $caringtype->name,
                        $caringtype->description,
                        $patient->name,
                        $caring->time->toDayDateTimeString(),
                        $caring->description
                    ];
                }
            }
        }
        //return response($result);
        return view('nurse',compact('result'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(nurse $nurse)
    {
        return view('nurse.edit',compact('nurse'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(ProfileUpdateRequest $request, nurse $nurse)
    {
        $this->saveRequestData($request, $nurse);
        Toast::success('Nurse Updated Successfully');
        return redirect()->back();
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(nurse $nurse)
    {
        $nurse->delete();
        Toast::success('Nurse Deleted Successfully');
        return redirect()->back();
    }

    public function updateToken(Request $request){
        try{
            $request->user()->update(
                [
                    'fcm_token'=>$request->token
                ]);
            return response()->json([
                'success'=>true,
                'token'=>$request->token,
                'user'=>$request->user()->name
            ]);
        }catch(\Exception $e){
            report($e);
            return response()->json([
                'success'=>false
            ],500);
        }
    }
}
