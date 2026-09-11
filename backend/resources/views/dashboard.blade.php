<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
    {{--            {{ __('Dashboard') }}--}}
                Hello {{explode(" ", auth()->user()?->getAttributeValue('name'))[0]}}
            </h2>
        </div>
    </x-slot>
    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                <div class="p-6 bg-white border-b border-gray-200">
                    <x-splade-table :for="$carings">
                        <x-splade-cell nurse_id>
                            {{ $item->nurse->name }}
                        </x-splade-cell>
                        <x-splade-cell patient_id>
                            {{ $item->patient->name }}
                        </x-splade-cell>
                        <x-splade-cell caringtype_id>
                            {{ $item->caringtype->name }}
                        </x-splade-cell>
                        <x-splade-cell time>
                            {{ $item->time->format('Y-m-d g:i A') }}
                        </x-splade-cell>
                        <x-splade-cell is_finished>
                            @if($item->is_finished)
                                {{ ('Finished') }}
                            @else
                                {{ ('Pending') }}
                            @endif
                        </x-splade-cell>
                        <x-splade-cell action>
                            @if(auth()->user()?->getAttributeValue('is_admin')==1)
                            <Link modal href="{{route('carings.edit',$item)}}" class="font-bold text-indigo-600"> Edit </Link>
                            &nbsp
                            <Link href="{{route('carings.destroy',$item)}}" method="DELETE"
                                  class="font-bold text-indigo-600"
                                  confirm="Delete" confirm-text="Are you sure you want to delete">
                                Delete
                            </Link>
                            @endif
                            &nbsp
                            <Link method="PATCH" href="{{route('carings.check',$item->id)}}" class="font-bold text-indigo-600"> Check </Link>
                        </x-splade-cell>
                    </x-splade-table>
                </div>
            </div>
        </div>
    </div>
</x-app-layout>
