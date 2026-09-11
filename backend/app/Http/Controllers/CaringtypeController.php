<?php

namespace App\Http\Controllers;

use App\Http\Requests\CaringtypeStoreRequest;
use App\Models\Caringtype;
use App\Tables\Caringtypes;
use App\Traits\CaringTypeTrait;
use Illuminate\Http\Request;
use ProtoneMedia\Splade\Facades\Toast;

class CaringtypeController extends Controller
{
    use CaringTypeTrait;
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('caringtype.index',['caringtypes' => Caringtypes::class]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('caringtype.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(CaringtypeStoreRequest $request)
    {
        $caringtype=new caringtype();
        $this->saveRequestData($request, $caringtype);
        Toast::success('Caring Type Added Successfully');
        return redirect()->back();
    }

    /**
     * Display the specified resource.
     */
    public function show(Caringtype $caringtype)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Caringtype $caringtype)
    {
        return view('caringtype.edit',compact('caringtype'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(CaringtypeStoreRequest $request, Caringtype $caringtype)
    {
        $this->saveRequestData($request, $caringtype);
        Toast::success('Caring Type Updated Successfully');
        return redirect()->back();
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Caringtype $caringtype)
    {
        $caringtype->delete();
        Toast::success('Caring Type Deleted Successfully');
        return redirect()->back();
    }
}
