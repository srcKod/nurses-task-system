<?php

namespace App\Http\Controllers;

use App\Http\Requests\PatientStoreRequest;
use App\Models\Patient;
use App\Tables\Patients;
use App\Traits\PatientTrait;
use Illuminate\Http\Request;
use ProtoneMedia\Splade\Facades\Toast;

class PatientController extends Controller
{
    use PatientTrait;
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('patient.index', ['patients' => Patients::class]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('patient.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(PatientStoreRequest $request)
    {
        $patient = new patient();
        $this->saveRequestData($request, $patient);
        Toast::success('Patient Added Successfully');
        return redirect()->back();
    }

    /**
     * Display the specified resource.
     */
    public function show(Patient $patient)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Patient $patient)
    {
        return view('patient.edit',compact('patient'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(PatientStoreRequest $request, Patient $patient)
    {
        $this->saveRequestData($request, $patient);
        Toast::success('Patient Updated Successfully');
        return redirect()->back();
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Patient $patient)
    {
        $patient->delete();
        Toast::success('Patient Deleted Successfully');
        return redirect()->back();
    }
}
